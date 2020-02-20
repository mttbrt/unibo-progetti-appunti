package environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environment.behavior.BehavioralSTEntry;
import environment.behavior.BehavioralType;
import environment.compilation.CompilationFunctionSTEntry;
import environment.compilation.CompilationVariableSTEntry;


public class Environment {
	
	/* The symbol table has been implemented with a list of hash tables, the reason is that we suppose 
	 * the order of nested scopes is a lot smaller than the number of names in the table
	 */
	private ArrayList<HashMap<String, BaseSTEntry>> variableSymbolTable;
	private ArrayList<HashMap<String, BaseSTEntry>> functionSymbolTable;
	private int currentLevel;
	private List<BaseSTEntry> parameterBuffer;
	private int labels;
	private ArrayList<Integer> localVars; //counts the number of local variables (needed for offset computation)
	
	//new empty environment
	public Environment() {
		this.variableSymbolTable = new ArrayList<HashMap<String, BaseSTEntry>>();
		this.functionSymbolTable = new ArrayList<HashMap<String, BaseSTEntry>>();
		this.currentLevel = -1;
		this.parameterBuffer = new ArrayList<BaseSTEntry>();
		this.labels = 0;
		this.localVars = new ArrayList<Integer>();
	}
	
	//SEMANTIC ANALYSIS
	
	public boolean addBaseVariable(String id, Type type) {
		if(!variableSymbolTable.get(0).containsKey(id) && !functionSymbolTable.get(0).containsKey(id)) {
			variableSymbolTable.get(0).put(id, new BaseVariableSTEntry(id, currentLevel, type));
			return true;
		}
		return false;	
	}
	
	public boolean addBaseFunction(String id, List<ParameterEntry> parameters) {
		if(!variableSymbolTable.get(0).containsKey(id) && !functionSymbolTable.get(0).containsKey(id)) {
			functionSymbolTable.get(0).put(id, new BaseFunctionSTEntry(id, currentLevel, parameters));
			return true;
		}
		return false;	
	}
	
	public boolean deleteId(String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			if(variableSymbolTable.get(i).containsKey(id)) {
				variableSymbolTable.get(i).remove(id);
				return true;
			} else if(functionSymbolTable.get(i).containsKey(id)) {
				functionSymbolTable.get(i).remove(id);
				return true;
			}
		}
		return false;
	}
	
	public boolean variableIsDeclared(String id) {
		for(int i = 0; i < variableSymbolTable.size(); ++i)
			if(variableSymbolTable.get(i).containsKey(id))
				return true;
		return false;
	}
	
	public boolean functionIsDeclared(String id) {
		for(int i = 0; i < functionSymbolTable.size(); ++i)
			if(functionSymbolTable.get(i).containsKey(id))
				return true;
		return false;
	}
	
	public int getInnermostDeclarationLevel(String id) {
		for(int i = 0; i <= currentLevel; ++i)
			if(variableSymbolTable.get(i).containsKey(id) || functionSymbolTable.get(i).containsKey(id))
				return i;
		return -1;
	}
	
	public List<ParameterEntry> getParameterEntries(String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			BaseFunctionSTEntry entry = (BaseFunctionSTEntry) functionSymbolTable.get(i).get(id);
			if(entry != null)
				return entry.getParameters();
		}
		return null;
	}
	
	public Type getVariableType(String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			BaseVariableSTEntry vste = (BaseVariableSTEntry)variableSymbolTable.get(i).get(id);
			if(vste != null) return vste.getType();
		}
		return Type.UNDEFINED;
	}
	
	/*
	 * Adds a variable entry with type for a parameter to the buffer, which will be used to populate
	 * the next opening scope (function body). Used in exercise 1. 
	 */
	public boolean bufferParameter(String id, Type type) {
		if(!parameterBuffer.stream().anyMatch(sve -> sve.getId()==id)) {
			parameterBuffer.add(new BaseVariableSTEntry(id, currentLevel+1, type));
			return true;
		}
		return false;	
	}
	
	public void enterNewScope() {
		++currentLevel;
		
		//Init new hashmap with buffered parameters (if any) for variables
		HashMap<String, BaseSTEntry> newScope = new HashMap<String, BaseSTEntry>();
		for(BaseSTEntry vste : parameterBuffer)
			newScope.put(vste.id, vste);
		parameterBuffer.clear();
		variableSymbolTable.add(0, newScope);
		
		//Init new empty hashmap for functions
		functionSymbolTable.add(0, new HashMap<String, BaseSTEntry>());
		
		//start counting local variables from 0
		localVars.add(0, 0);
	}
	
	public void exitScope() {
		--currentLevel;
		variableSymbolTable.remove(0);
		functionSymbolTable.remove(0);
		localVars.remove(0);
	}
	
	
	//BEHAVIORAL ANALYSIS
	
	public BehavioralSTEntry addOrUpdateBehavioralVariable(String id) {
		BehavioralSTEntry newEntry = new BehavioralSTEntry(id, currentLevel);
		//We always add, regardless of the state of the table
		variableSymbolTable.get(0).put(id, newEntry);

		return newEntry;
	}
	
	public BehavioralSTEntry addOrUpdateBehavioralFunction(String id, BehavioralType behavioralType) {
		BehavioralSTEntry newEntry = new BehavioralSTEntry(id, currentLevel, behavioralType);
		//We always add, regardless of the state of the table
		functionSymbolTable.get(0).put(id, newEntry);

		return newEntry;
	}
	
	public boolean isDeclaredLocally(String id) {
		return variableSymbolTable.get(0).containsKey(id) || functionSymbolTable.get(0).containsKey(id);	
	}
	
	public boolean bindReadWrite(BehavioralType behavioralType, String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			if(variableSymbolTable.get(i).containsKey(id)) {
				behavioralType.addReadWrite(variableSymbolTable.get(i).get(id));
				return true;
			} else if (functionSymbolTable.get(i).containsKey(id))  {
				behavioralType.addReadWrite(functionSymbolTable.get(i).get(id));
				return true;
			}
		}
		return false;
	}
	
	public boolean bindDeletion(BehavioralType behavioralType, String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			if(variableSymbolTable.get(i).containsKey(id)) {
				behavioralType.addDeletion(variableSymbolTable.get(i).get(id));
				return true;
			} else if (functionSymbolTable.get(i).containsKey(id))  {
				behavioralType.addDeletion(functionSymbolTable.get(i).get(id));
				return true;
			}
		}
		return false;
	}
	
	public BaseSTEntry getVariableReference(String id) {
		for(int i = 0; i < variableSymbolTable.size(); ++i) {
			if(variableSymbolTable.get(i).containsKey(id)) {
				return variableSymbolTable.get(i).get(id);
			}
		}
		return null;
	}
	
	public BehavioralType getFunctionBehavioralType(String id) {
		for(int i = 0; i <= currentLevel; ++i) {
			BehavioralSTEntry fste = (BehavioralSTEntry) functionSymbolTable.get(i).get(id);
			if(fste != null)
				return fste.getBehavioralType();
		}
		return null;
	}	
	
	public BehavioralSTEntry bufferBehavioralParameter(String id) {
		if(!parameterBuffer.stream().anyMatch(sve -> sve.getId()==id)) {
			BehavioralSTEntry newEntry = new BehavioralSTEntry(id, currentLevel+1);
			parameterBuffer.add(newEntry);
			return newEntry;
		}
		return null;	
	}
	
	public List<BaseSTEntry> copyParameterBuffer() {
		return new ArrayList<BaseSTEntry>(parameterBuffer);
	}
	
	public void restoreParameterBuffer(List<BaseSTEntry> parameterBuffer) {
		this.parameterBuffer = parameterBuffer;
	}
	
	//CODE GENERATION
	
	public int addOrUpdateCompilationVariable(String id) {
		int newLocalVars = localVars.get(0) + 1;
		localVars.set(0, newLocalVars);
		variableSymbolTable.get(0).put(id, new CompilationVariableSTEntry(id, currentLevel, -newLocalVars-1, false, false));
		return -newLocalVars-1; //Returns the offset
	}
	
	public String addOrUpdateCompilationFunction(String id, List<ParameterEntry> parameters) {
		String functionLabel = newLabel();
		functionSymbolTable.get(0).put(id, new CompilationFunctionSTEntry(id, currentLevel, parameters, functionLabel));
		return functionLabel; //Returns function label
	}
	
	public void bufferCompilationParameter(String id, boolean var) {
		int currentNumberOfParameters = parameterBuffer.size();
		parameterBuffer.add(new CompilationVariableSTEntry(id, currentLevel+1, currentNumberOfParameters+1, true, var));
	}
	
	public String newLabel() {
		return "LABEL_" + (labels++);
	}
	
	public int getRelativeDepth(String id) { //Maybe remove, although logically different
		return getInnermostDeclarationLevel(id);
	}
	
	public int getNumberOfLocalVariables() {
		return localVars.get(0);
	}

	public boolean isVariableReference(String id) {
		for(int i = 0; i <= currentLevel; ++i)
			if(variableSymbolTable.get(i).containsKey(id))
				return ((CompilationVariableSTEntry)variableSymbolTable.get(i).get(id)).isReference();
		return false;
	}
	
	public int getVariableOffset(String id) {
		for(int i = 0; i <= currentLevel; ++i)
			if(variableSymbolTable.get(i).containsKey(id))
				return ((CompilationVariableSTEntry)variableSymbolTable.get(i).get(id)).getOffset();
		return -1; //Should never happen at this stage
	}
	
	public String getFunctionLabel(String id) {
		for(int i = 0; i <= currentLevel; ++i)
			if(functionSymbolTable.get(i).containsKey(id))
				return ((CompilationFunctionSTEntry)functionSymbolTable.get(i).get(id)).getLabel();
		return null; //Should never happen at this stage
	}
	
}


