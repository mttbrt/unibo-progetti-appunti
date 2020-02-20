package model;

import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class ExpValue extends Value {
	
	private Exp expression;
	
	public ExpValue(Exp expression) {
		this.expression = expression;
	}
	
	public Exp getExp() {
		return expression;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		return expression.checkSemantics(env, allowDeletion);
	}

	@Override
	public Type checkType(Environment env) {
		return expression.checkType(env);
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		return expression.checkBehavioralType(env);
	}
	
	@Override
	public boolean isVariable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + expression.toString() + ")";
	}

	@Override
	public String generateWTMCode(Environment env) {
		return expression.generateWTMCode(env);
	}
}
