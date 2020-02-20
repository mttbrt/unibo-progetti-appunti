package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class VariableValue extends Value {
	
	private String id;
	
	public VariableValue(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		List<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		if(!env.variableIsDeclared(id)) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.UNDECLARED_VAR, "Semantic error: variable \"" + id + "\" not declared.");
			semanticErrors.add(error);
		}
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		return env.getVariableType(id);
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		BehavioralType behavioralType = new BehavioralType();
		env.bindReadWrite(behavioralType, id);
		return behavioralType;
	}
	
	@Override
	public boolean isVariable() {
		return true;
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public String generateWTMCode(Environment env) {
		int relativeDepth = env.getRelativeDepth(id);
		int offset = env.getVariableOffset(id);
		String code = "";
		if (relativeDepth == 0) {
			//variable is local, access directly from local AR
			code += "lw $a0 " + offset + "($fp)\n";
		} else {
			//external variable, climb static chain
			code += "lw $al 0($fp)\n";
			for(int i = 1; i < relativeDepth; i++) {
				code += "lw $al 0($al)\n";
			}
			code += "lw $a0 " + offset + "($al)\n";
		}

		if (env.isVariableReference(id)) code += "lw $a0 0($a0)\n"; //dereference references
		return code;
	}
	
}
