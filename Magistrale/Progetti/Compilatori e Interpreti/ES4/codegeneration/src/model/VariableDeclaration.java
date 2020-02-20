package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralSTEntry;
import environment.behavior.BehavioralType;
import util.SemanticError;

public class VariableDeclaration extends Statement {

	private String id;
	private Type type;
	private Exp exp;
	
	public VariableDeclaration(Type type, String id, Exp exp) {
	    this.id = id;
	    this.type = type;
	    this.exp = exp;
	}
	
	public String getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
  		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
  		
  		//Check variable freshness
  		if(!env.addBaseVariable(id, type)) {
  			SemanticError error = new SemanticError(SemanticError.ErrorType.MULTIPLE_DECLARATION, "Semantic error: in statement \"" + this.toString() + "\", variable id \"" + id + "\" is already declared in the same scope");
  			if(!allowDeletion) {
  				//in case of allowed deletions, this is taken care of by checkBehavioralType
  				semanticErrors.add(error);
  			}
  			return semanticErrors;	//bail out
  		}
  		
  		semanticErrors.addAll(exp.checkSemantics(env, allowDeletion));
        
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		env.addBaseVariable(id, type);	//semantics have been checked, this will not cause any error
		
		Type expType = exp.checkType(env);
		if(type == expType)
			return type;
		
		if(expType != null)
			System.out.println("Type error: cannot assign \"" + exp.toString() + "\" of type " + expType.toString() + " to variable \"" + id + "\" of type " + type.toString() + " in statement \"" + this.toString() + "\".");
		
		return null;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		boolean redeclaration = env.isDeclaredLocally(id);
		BehavioralSTEntry newEntry = env.addOrUpdateBehavioralVariable(id);
		BehavioralType declarationType = exp.checkBehavioralType(env);

		if (redeclaration) {
			declarationType.addRedeclaration(newEntry);
		} else {
			declarationType.addDeclaration(newEntry);
		}

		return declarationType;
	}

	@Override
	public String toString() {
		return type.toString() + " " + id + " = " + exp.toString() + ";";
	}

	@Override
	public String generateWTMCode(Environment env) {
		String code = exp.generateWTMCode(env);
		env.addOrUpdateCompilationVariable(id);
		code += "push $a0\n"; //local variables grow on the stack
		return code;
	}
	
}
