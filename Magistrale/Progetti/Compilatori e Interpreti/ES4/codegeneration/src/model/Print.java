package model;

import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class Print extends Statement {
	
	private Exp expression;
	
	public Print(Exp expression) {
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
		if(expression.checkType(env) != null)
			return Type.VOID;
		return null;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		return expression.checkBehavioralType(env);
	}

	@Override
	public String toString() {
		return "print " + expression.toString() + ";";
	}

	@Override
	public String generateWTMCode(Environment env) {
		return expression.generateWTMCode(env)
				+ "print $a0 \n";
	}
	
	

}
