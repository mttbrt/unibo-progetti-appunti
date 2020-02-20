package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import environment.behavior.Consistency;
import environment.behavior.ConsistencyResult;
import util.SemanticError;

public class IfThenElse extends Statement {
	
	private Exp condition;
	private Block thenBranch;
	private Block elseBranch;
	
	public IfThenElse(Exp condition, Block thenBranch, Block elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	public Exp getCondition() {
		return condition;
	}

	public Node getThenBranch() {
		return thenBranch;
	}

	public Node getElseBranch() {
		return elseBranch;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		semanticErrors.addAll(condition.checkSemantics(env, allowDeletion));
		semanticErrors.addAll(thenBranch.checkSemantics(env, allowDeletion));	
		semanticErrors.addAll(elseBranch.checkSemantics(env, allowDeletion));
				
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		Type cond = condition.checkType(env);
		if(cond == Type.BOOL) {
			thenBranch.checkType(env);
			elseBranch.checkType(env);
			return Type.VOID;
		} 

		if(cond != null)
			System.out.println("Type error: in IF condition expected BOOL, found " + cond);
		
		return null;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		BehavioralType behavioralType = condition.checkBehavioralType(env);
		BehavioralType thenType = thenBranch.checkBehavioralType(env);
		BehavioralType elseType = elseBranch.checkBehavioralType(env);
		
		if(thenType != null && elseType != null) {
			if(thenType.isITECompatible(elseType)) {
				Consistency result = behavioralType.incrementType(thenType);
				if(result.getResult() == ConsistencyResult.OK) {
					return behavioralType;
				} else {
					System.out.println("Behavioral error: condition \"" + condition.toString() + "\" of ITE statement makes use of deleted variable " + result.getOffendingId());
					return null;
				}
			} else if (elseType.isTentative()) {
				Consistency result = behavioralType.incrementType(thenType);
				//Else branch needs a second pass: increment conditionType with thenType				
				if(result.getResult() == ConsistencyResult.OK) {
					behavioralType.setTentative(true);
					return behavioralType;
				} else {
					System.out.println("Behavioral error: then branch of ITE statement makes use of deleted variable " + result.getOffendingId());
				}
			} else if (thenType.isTentative()) {
				Consistency result = behavioralType.incrementType(elseType);
				//Then branch needs a second pass: increment conditionType with elseType
				if(result.getResult() == ConsistencyResult.OK) {
					behavioralType.setTentative(true);
					return behavioralType;
				} else {
					System.out.println("Behavioral error: else branch of ITE statement makes use of deleted variable " + result.getOffendingId());
				}
			} else {
				System.out.println("Behavioral error: ITE branches " + thenBranch.toString() + " and " + elseBranch.toString() + " do not have the same behavior.");
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "if (" + condition.toString() + ") then " + thenBranch.toString() + " else " + elseBranch.toString();
	}

	@Override
	public String generateWTMCode(Environment env) {
		String elseLabel = env.newLabel();
		String endLabel = env.newLabel();
		return condition.generateWTMCode(env)
				+ "li $t1 0\n"
				+ "beq $t1 $a0 " + elseLabel + "\n"
				+ thenBranch.generateWTMCode(env)
				+ "b " + endLabel + "\n"
				+ elseLabel + ":\n"
				+ elseBranch.generateWTMCode(env)
				+ endLabel + ":\n";
	}
	
	

}
