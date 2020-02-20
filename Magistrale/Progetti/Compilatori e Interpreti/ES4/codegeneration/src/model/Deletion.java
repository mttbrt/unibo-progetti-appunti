package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class Deletion extends Statement {
	
	private String id;
	
	public Deletion(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		if(!env.variableIsDeclared(id) && !env.functionIsDeclared(id)) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.UNDECLARED_VAR, "Semantic error: undeclared id \"" + id + "\" in statement \"" + this.toString() + "\".");
  			semanticErrors.add(error);
  			return semanticErrors;	//bail out
		}
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		return Type.VOID;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		
		BehavioralType behavioralType = new BehavioralType();
		env.bindDeletion(behavioralType, id);
		
		return behavioralType;
	}

	@Override
	public String toString() {
		return "delete " + id + ";";
	}

	@Override
	public String generateWTMCode(Environment env) {
		/*
		 * Deletion is treated as a logical construct. As such, if a program has passed static analysis,
		 * deletion is transparent to the WTM
		 */
		return "";
	}
	
	

}
