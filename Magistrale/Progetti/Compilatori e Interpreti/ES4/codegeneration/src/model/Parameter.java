package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralSTEntry;
import environment.behavior.BehavioralType;
import model.Node;
import util.SemanticError;

public class Parameter extends Node {

	private boolean var;
	private Type type;
	private String id;
	
	public Parameter(boolean var, Type type, String id) {
		this.var = var;
	    this.id = id;
	    this.type = type;
	}
	
	public boolean isVar() {
		return var;
	}
	
	public String getId() {
		return id;
	}

	public Type getType() {
		return type;
	}
	
	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		List<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		if(!env.bufferParameter(id, type)) {
  			SemanticError error = new SemanticError(SemanticError.ErrorType.MULTIPLE_DECLARATION, "Semantic error: parameter id \"" + id + "\" already declared.");
  			semanticErrors.add(error);
  		}
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		if(env.bufferParameter(id, type))	//semantics have been checked, this will not cause any error
			return type;
		return null;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		BehavioralSTEntry newEntry = env.bufferBehavioralParameter(id); //semantics have been checked, this will not cause any error
		BehavioralType parameterType = new BehavioralType();
		if(!var) {
			parameterType.addDeclaration(newEntry);
		}
		return parameterType;
	}

	@Override
	public String toString() {
		return (var ? "var " : "") + type.toString() + " " + id;
	}

	@Override
	public String generateWTMCode(Environment env) {
		env.bufferCompilationParameter(id, var);
		return "";
	}
	
}
