package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.ParameterEntry;
import environment.BaseSTEntry;
import environment.Type;
import environment.behavior.BehavioralType;
import environment.behavior.Consistency;
import environment.behavior.ConsistencyResult;
import environment.behavior.ParametricBehavioralType;
import util.SemanticError;

public class FunctionCall extends Statement {

	private String id;
	private List<Exp> arguments; 
	
	public FunctionCall(String id, List<Exp> arguments) {
		this.id = id;
		this.arguments = arguments;
	}

	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
			
		//Check function existence
		if(!env.functionIsDeclared(id)) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.UNDECLARED_FUN, "Semantic error: undeclared function \"" + id + "\" called in statement \"" + this.toString() + "\".");
			semanticErrors.add(error);
			return semanticErrors;	//bail out
		}
		
		//Check argument number
		List<ParameterEntry> parameters = env.getParameterEntries(id);
		if(parameters.size() != arguments.size()) {
			SemanticError error = new SemanticError(SemanticError.ErrorType.PARAMETER_MISMATCH, "Semantic error: function \"" + id + "\" expects " + parameters.size() + " parameters, " + arguments.size() + " found in statement \"" + this.toString() + "\".");
			semanticErrors.add(error);
			return semanticErrors;	//bail out
		}
		
		//Check arguments
		for(int i = 0; i < arguments.size(); i++) {
			semanticErrors.addAll(arguments.get(i).checkSemantics(env, allowDeletion));
			if(parameters.get(i).isVar() && !arguments.get(i).isVariable()) {
				SemanticError error = new SemanticError(SemanticError.ErrorType.REFERENCE_ERROR, "Semantic error: cannot pass \"" + arguments.get(i) + "\" as a reference in statement \"" + this.toString() + "\".");
				semanticErrors.add(error);
			}
		}

		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		List<ParameterEntry> parameters = env.getParameterEntries(id);
		
		for(int i = 0; i < arguments.size(); i++) {
			Type arg = arguments.get(i).checkType(env);
			if(arg != parameters.get(i).getType()) {
				System.out.println("Type error: parameter \"" + parameters.get(i).getId() + "\" in function call \"" + this.toString() + "\" is of type " + arg + ", should have been of type " + parameters.get(i).getType()+".");
				return null;
			}
		}
		
		return Type.VOID;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		ParametricBehavioralType functionType = (ParametricBehavioralType) env.getFunctionBehavioralType(id);
		
		if(functionType == null) {
			//This is the case of a recursive call to a function in its own body
			//In this case, the function's behavior is still undefined
			//We return an empty behavioral type, specifying it is tentative (basically an educated guess)
			BehavioralType tentativeBehavioralType = new BehavioralType();
			tentativeBehavioralType.setTentative(true);
			return tentativeBehavioralType;
		}
		
		BehavioralType callType = new BehavioralType();
		List<BaseSTEntry> referencedArguments = new ArrayList<BaseSTEntry>();
		
		for(int i = 0; i < arguments.size(); ++i) {
			if (functionType.getFormalParameters().get(i) != null) {
				//In this case we take a var argument
				//We do not need to check that this is a variable, as it is done in checkSemantics
				
				referencedArguments.add(env.getVariableReference(arguments.get(i).getVariableId()));
			} else {
				//In this case we have a value argument:
				Consistency result = callType.incrementType(arguments.get(i).checkBehavioralType(env));
				if(result.getResult() != ConsistencyResult.OK) {
					System.out.println("Behavioral error: actual parameter \"" + arguments.get(i).toString() + "\" accesses a deleted variable " + result.getOffendingId());
					return null;
				}

				referencedArguments.add(null); //value argument does not need to be instantiated
			}
		}
		
		// Instantiate formalParameters with function call's parameter entries
		callType.incrementType(functionType.instantiateType(referencedArguments));
		
		return callType;
	}

	@Override
	public String toString() {
		String str = id + "(";
		if(!arguments.isEmpty()) {
			for(Node par : arguments) {
				str += par.toString() + ", ";
			}
			str = str.trim();
			str = str.substring(0, str.length() - 1);
		}
		str += ");";
		
		return str;
	}

	@Override
	public String generateWTMCode(Environment env) {
		List<ParameterEntry> parameters = env.getParameterEntries(id);
		String functionLabel = env.getFunctionLabel(id);
		String code = "push $fp\n";
		for(int i = arguments.size()-1; i >= 0; i--) {
			if(parameters.get(i).isVar()) {
				String parameterId = arguments.get(i).getVariableId();
				boolean reference = env.isVariableReference(parameterId);
				int relativeDepth = env.getRelativeDepth(parameterId);
				int offset = env.getVariableOffset(parameterId);
				if (relativeDepth == 0) {
					//variable is local, access directly from local AR
					code += reference ? "lw $a0 " + offset + "($fp)\n" : "addi $a0 $fp " + offset + "\n" ;
				} else {
					//external variable, climb static chain
					code += "lw $al 0($fp)\n";
					for(int j = 1; j < relativeDepth; j++) {
						code += "lw $al 0($al)\n";
					}
					code += reference ? "lw $a0 " + offset + "($al)\n" : "addi $a0 $al " + offset + "\n" ;
				}
			} else {
				code += arguments.get(i).generateWTMCode(env);
			}
			code += "push $a0\n";
		}
		code += "lw $al 0($fp)\n";
		for(int i = 1; i < env.getRelativeDepth(id); i++) {
			code += "lw $al 0($al)\n";
		}
		code += "push $al\n"
				+ "jal " + functionLabel + "\n";
		
		return code;
	}
	
}
