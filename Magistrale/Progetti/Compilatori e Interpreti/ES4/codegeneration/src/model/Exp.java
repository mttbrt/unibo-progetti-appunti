package model;

import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.Operator;
import util.SemanticError;

public class Exp extends Node {
	
	private boolean negative;
	private Term term;
	private Operator op;
	private Exp expression;
	
	public Exp(boolean negative, Term term, Operator op, Exp expression) {
		this.negative = negative;
		this.term = term;
		this.op = op;
		this.expression = expression;
	}

	public boolean isNegative() {
		return negative;
	}

	public Term getTerm() {
		return term;
	}

	public Operator getOp() {
		return op;
	}

	public Exp getExpression() {
		return expression;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		List<SemanticError> semanticErrors = term.checkSemantics(env, allowDeletion);
		if(expression != null)
			semanticErrors.addAll(expression.checkSemantics(env, allowDeletion));
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		Type leftType = term.checkType(env);

		if(expression != null) {
			Type rightType = expression.checkType(env);
			if(rightType == null) {
				return null;
			}
			if(leftType == rightType) {
				if(leftType == Type.INT) {
					return Type.INT;
				}
				System.out.println("Type error: Operator \"" + op + "\" expects type INT in expression \"" + this.toString() + "\".");
				return null;
			}
			//left and right are not of the same type or one side did not type correctly.
			System.out.println("Type error: mismatch between \"" + term + "\" and \"" + expression + "\" in expression \"" + this.toString() + "\".");
			return null;
		}

		if (leftType == Type.BOOL && negative) {
			System.out.println("Type error: Operator \"-\" expects type INT in expression \"" + this.toString() + "\".");
			return null;
		}
		
		return leftType;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		//There can be no RW or D conflicts in an expression
		BehavioralType behavioralType = term.checkBehavioralType(env);
		if(expression != null) {
			behavioralType.incrementType(expression.checkBehavioralType(env));
		}
		return behavioralType;
	}
	
	public boolean isVariable() {
		if(!negative && expression==null) {
			return term.isVariable();
		}
		return false;
	}
	
	public String getVariableId() {
		if(isVariable()) {
			return ((VariableValue)term.getFactor().getLeft()).getId();
		}
		return null;
	}
	
	@Override
	public String toString() {
		if(expression!=null) {
			return term.toString() + op + expression.toString();
		} else {
			return term.toString();
		}
	}
	
	@Override
	public String generateWTMCode(Environment env) {
		String code = term.generateWTMCode(env);
		if (negative) {
			code += "csig $a0 $a0\n";
		}
		if (expression != null) {
			code += "push $a0\n"
					+ expression.generateWTMCode(env)
					+ "top $t1\n"
					+ "pop\n"
					+ op.toWTMInstruction() + " $a0 $t1 $a0\n";
		}
		
		return code;				
	}

}
