package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class BooleanValue extends Value {
	
	private boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		return new ArrayList<SemanticError>();	//A boolean value does not involve declarations
	}

	@Override
	public Type checkType(Environment env) {
		return Type.BOOL;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		//A value has no behavioral type: empty
		return new BehavioralType();
	}
	
	public boolean isVariable() {
		return false;
	}
	
	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	@Override
	public String generateWTMCode(Environment env) {
		return "li $a0 " + (value ? "1" : "0") + "\n";
	}

}
