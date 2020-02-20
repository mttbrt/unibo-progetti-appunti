package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class Assignment extends Statement {
	
	private String id;
	private Exp expression;
	
	public Assignment(String id, Exp expression) {
		this.id = id;
		this.expression = expression;
	}

	public String getId() {
		return id;
	}

	public Exp getExpression() {
		return expression;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		//Check variable existence
		if(!env.variableIsDeclared(id)) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.UNDECLARED_VAR, "Semantic error: undeclared variable \"" + id + "\" in statement \"" + this.toString() + "\".");
  			semanticErrors.add(error);
  			return semanticErrors;	//bail out
		}
		
		//Check assigned expression
		semanticErrors.addAll(expression.checkSemantics(env, allowDeletion));
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		Type type = env.getVariableType(id);
		Type expType = expression.checkType(env);

		if(expType == null) return null;
		
		if(expType != type) {
			System.out.println("Type error: cannot assign \"" + expression.toString() + "\" of type " + expType.toString() + " to variable \"" + id + "\" of type " + type.toString() + " in statement \"" + this.toString() + "\".");
			return null;
		}
		
		return Type.VOID;
	}
	
	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		BehavioralType assignmentType = new BehavioralType();
		env.bindReadWrite(assignmentType, id); //RW of variable referenced by id
		assignmentType.incrementType(expression.checkBehavioralType(env)); //RW of the expression
		return assignmentType;
	}

	@Override
	public String toString() {
		return id + " = " + expression.toString() + ";";
	}

	@Override
	public String generateWTMCode(Environment env) {
		int relativeDepth = env.getRelativeDepth(id);
		int offset = env.getVariableOffset(id);
		boolean reference = env.isVariableReference(id);
		String code = expression.generateWTMCode(env);
		if (relativeDepth == 0) {
			//variable is local
			//if reference, get address in $t1, else store directly
			code += reference ? "lw $t1 " + offset + "($fp)\n" : "sw $a0 " + offset + "($fp)\n";
		} else {
			//external variable, climb static chain
			code += "lw $al 0($fp)\n";
			for(int i = 1; i < relativeDepth; i++) {
				code += "lw $al 0($al)\n";
			}
			//if reference, get address in $t1, else store directly
			code += reference ? "lw $t1 " + offset + "($al)\n" : "sw $a0 0($al)\n";
		}
		
		//if variable was reference and address is in $t1, store in cell pointed by $t1
		if (reference) code += "sw $a0 0($t1)\n";
		return code;
	}
}
