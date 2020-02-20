package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.ParameterEntry;
import environment.BaseSTEntry;
import environment.Type;
import environment.behavior.BehavioralSTEntry;
import environment.behavior.BehavioralType;
import environment.behavior.ParametricBehavioralType;
import util.SemanticError;

public class FunctionDeclaration extends Statement {

	private String id;
	private List<Parameter> parametersList;
	private Block body;
		
	public FunctionDeclaration(String id, List<Parameter> parameterList, Block body) {
		this.id = id;
		this.parametersList = parameterList;
		this.body = body;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
				
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		 
		//Create function entry in symbol table
		ArrayList<ParameterEntry> parameterEntries = new ArrayList<ParameterEntry>();
		for(Parameter par : parametersList) 
			parameterEntries.add(new ParameterEntry(par));
		if(!env.addBaseFunction(id, parameterEntries)) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.MULTIPLE_DECLARATION, "Semantic error: function id \"" + id + "\" already declared in this scope.");
			if(!allowDeletion) {
  				//in case of allowed deletions, this is taken care of by checkBehavioralType
  				semanticErrors.add(error);
  			}
			return semanticErrors; //bail out
		}
		
		// Check function parameters: this adds them to the parameter buffer
		for(Parameter par : parametersList)
			semanticErrors.addAll(par.checkSemantics(env, allowDeletion));
			
		// Check function body
		semanticErrors.addAll(body.checkSemantics(env, allowDeletion));
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
	
		//Create function entry in symbol table
		ArrayList<ParameterEntry> parameterEntries = new ArrayList<ParameterEntry>();
		for(Parameter par : parametersList) 
			parameterEntries.add(new ParameterEntry(par));
		env.addBaseFunction(id, parameterEntries);	//semantics have been checked, this will not cause any error
				
		for(Parameter par : parametersList)
			if(par.checkType(env) == null)
				return null;	//Fail to type if a parameter is duplicated (should never happen)
		
		if(body.checkType(env) == null) {
			return null;	//Fail to type if the body does not type
		} else {
			return Type.VOID; //If typing of the body is successful, return void
		}
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		
		boolean redeclaration = env.isDeclaredLocally(id);
		ParametricBehavioralType functionType = new ParametricBehavioralType();
	
		for(Parameter par : parametersList) {
			//This call adds the parameters to the parameter buffer
			//And adds a declaration for the parameter as a subtype to the function type
			functionType.incrementType(par.checkBehavioralType(env));
		}
		
		// Add formal parameters to function type
		for(Parameter param : parametersList) {
			functionType.addFormalParameter(param.getId());
		}
		
		//In case of recursion, we will need to restore these values
		List<BaseSTEntry> parameterBuffer = env.copyParameterBuffer();
		ParametricBehavioralType hollowFunctionType = new ParametricBehavioralType(functionType);
		
		// Body block behavioral type
		BehavioralType blockType = body.checkBehavioralType(env);
		if(blockType == null) return null; //Block was not consistent
		
		//Add block type to function type
		functionType.incrementType(blockType);
		functionType.cleanType();
		
		BehavioralSTEntry newEntry = env.addOrUpdateBehavioralFunction(id, functionType);
		
		if(blockType.isTentative()) {
			//We encountered recursion, a second pass is needed
			//We restore the parameters, as well as the function type, and we check the body again
			env.restoreParameterBuffer(parameterBuffer);
			functionType = hollowFunctionType;
			
			blockType = body.checkBehavioralType(env);
			if(blockType == null) return null; //Block was not consistent during second pass
			
			//This time blockType will not be tentative
			functionType.incrementType(blockType);
			functionType.cleanType();
			
			newEntry = env.addOrUpdateBehavioralFunction(id, functionType);
		}
		
		BehavioralType declarationType = new BehavioralType();
		if(redeclaration) {
			declarationType.addRedeclaration(newEntry);
		} else {
			declarationType.addDeclaration(newEntry);
		}
		
		return declarationType;		
	}

	@Override
	public String toString() {
		String str = id + "(";
		if(!parametersList.isEmpty()) {
			for(Parameter par : parametersList) {
				str += par.toString() + ", ";
			}
			str = str.trim();
			str = str.substring(0, str.length() - 1);
		}
		str += ")";	
		str += body.toString();
		
		return str;
	}

	@Override
	public String generateWTMCode(Environment env) {
		//get parameters
		List<ParameterEntry> parameters = new ArrayList<ParameterEntry>();
		for(Parameter p : parametersList) {
			parameters.add(new ParameterEntry(p));
		}
		String functionLabel = env.addOrUpdateCompilationFunction(id, parameters);
		
		//Just adds parameters to the buffer
		for(Parameter p : parametersList) {
			p.generateWTMCode(env);
		}
		
		//Create skip label, used to avoid executing the declared code unless called
		String skipLabel = env.newLabel();
		
		String code = "b " + skipLabel + "\n"
				+ functionLabel + ":\n"
				+ "move $fp $sp\n"
				+ "push $ra\n";
		
		//Bypass block code generation
		//(regular blocks handle their own AL, oldFP and so on, therefore we cannot recur directly)
		env.enterNewScope();
		for (Statement st : body.getStatements()) {
			code += st.generateWTMCode(env);
		}
		int localVars = env.getNumberOfLocalVariables();
		if (localVars > 0) {
			code += "addi $sp $sp " + localVars + "\n";	//Undo local variables on the stack
		}
		env.exitScope();
		
		code += "top $ra\n" //load RA
				+ "addi $sp $sp " + (parametersList.size()+2) + "\n" //undo RA, AL and arguments on the stack 
				+ "top $fp\n"
				+ "pop\n"
				+ "jr $ra\n"
				+ skipLabel + ":\n";
		
		return code;
	}
	
}
