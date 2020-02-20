package model;

import java.util.List;

import environment.Environment;
import environment.behavior.BehavioralType;
import util.SemanticError;

public abstract class Factor extends Node {
	
	protected Value left;
	protected Value right;
	
	protected Factor(Value left, Value right) {
		this.left = left;
		this.right = right;
	}

	public Value getLeft() {
		return left;
	}

	public Value getRight() {
		return right;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		List<SemanticError> semanticErrors = left.checkSemantics(env, allowDeletion);
		if(right!=null)
			semanticErrors.addAll(right.checkSemantics(env, allowDeletion));
		
		return semanticErrors;
	}
	
	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		//There can be no RW or D conflicts in a factor
		BehavioralType behavioralType = left.checkBehavioralType(env);
		if (right != null) {
			behavioralType.incrementType(right.checkBehavioralType(env));
		}
		return behavioralType;
	}
	
	public boolean isVariable() {
		if (right == null) {
			return left.isVariable();
		}
		return false;
	}
	
}
