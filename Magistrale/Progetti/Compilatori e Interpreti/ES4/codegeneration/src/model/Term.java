package model;

import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.Operator;
import util.SemanticError;

public class Term extends Node {
	
	private Factor factor;
	private Operator op;
	private Term term;
	
	public Term(Factor factor, Operator op, Term term) {
		this.factor = factor;
		this.op = op;
		this.term = term;
	}

	public Factor getFactor() {
		return factor;
	}

	public Operator getOp() {
		return op;
	}

	public Term getTerm() {
		return term;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		List<SemanticError> semanticErrors = factor.checkSemantics(env, allowDeletion);
		if(term != null)
			semanticErrors.addAll(term.checkSemantics(env, allowDeletion));
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		Type leftType = factor.checkType(env);
		
		if(term != null) {
			Type rightType = term.checkType(env);
			if(rightType == null) {
				return null;
			}
			if(leftType == rightType) {
				if (leftType == Type.INT) {
					return Type.INT;
				}
				System.out.println("Type error: operator \"" + op + "\" expects type INT in expression \"" + this.toString() + "\".");
				return null;
			}
			//left and right are not of the same type or one side did not type correctly.
			System.out.println("Type error: type mismatch between \"" + factor + "\" and \"" + term + "\" in expression \"" + this.toString() + "\".");
			return null;
		}
		
		return leftType;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		//There can be no RW or D conflicts in a term
		BehavioralType behavioralType = factor.checkBehavioralType(env);
		if(term != null) {
			behavioralType.incrementType(term.checkBehavioralType(env));
		}
		return behavioralType;
	}
	
	public boolean isVariable() {
		if (term == null) {
			return factor.isVariable();
		}
		return false;
	}
	
	@Override
	public String toString() {
		if(term!=null) {
			return factor.toString() + op + term.toString();
		} else {
			return factor.toString();
		}
	}

	@Override
	public String generateWTMCode(Environment env) {
		String code = factor.generateWTMCode(env);
		if (term != null) {
			code += "push $a0\n"
					+ term.generateWTMCode(env)
					+ "top $t1\n"
					+ "pop\n"
					+ op.toWTMInstruction() + " $a0 $t1 $a0\n";
		}
		
		return code;
	}

}
