package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class IntegerValue extends Value {
	
	private int value;
	
	public IntegerValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		return new ArrayList<SemanticError>();	//A boolean value does not involve declarations
	}

	@Override
	public Type checkType(Environment env) {
		return Type.INT;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		//A value has no behavioral type: empty
		return new BehavioralType();
	}
	
	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public String generateWTMCode(Environment env) {
		return "li $a0 " + value + "\n";
	}

}
