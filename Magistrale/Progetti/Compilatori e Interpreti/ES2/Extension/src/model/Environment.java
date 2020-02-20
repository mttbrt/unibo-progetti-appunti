package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {

	/* Contains a table of (identifier,times) declarations, where type can be
	 * times counts the number of times identifier is declared.*/
	private HashMap<String,Integer> variableDeclarations = new HashMap<String,Integer>();
	private HashMap<String,Integer> functionDeclarations = new HashMap<String,Integer>();
	
	/* Updates the number of variables declarations in the variables HashMap
	 * @param 	id	variable identifier.*/
	public void addVariableDeclaration(String id) {
		Integer times = variableDeclarations.get(id);
		if (times != null)
			variableDeclarations.put(id, times + 1);
		else
			variableDeclarations.put(id, 1);
	}
	
	/* Updates the number of functions declarations in the functions HashMap
	 * @param 	id	function identifier.*/
	public void addFunctionDeclaration(String id) {
		Integer times = functionDeclarations.get(id);
		if (times != null)
			functionDeclarations.put(id, times + 1);
		else
			functionDeclarations.put(id, 1);
	}
	
	/* Returns the number of functions declarations by summing each element in the functions HashMap
	 * @return number of functions declarations
	 */
	public int getNumberOfFunctionDeclarations() {
		return functionDeclarations.size() > 0 ? functionDeclarations.values().stream().reduce((a,b)->a+b).get() : 0;
	}
	
	/* Returns an ArrayList with duplicate identifiers by merging the two HashMaps above
	 * @return number of duplicated identifiers
	 */
	public ArrayList<String> getDuplicates() {
		ArrayList<String> duplicates = new ArrayList<>();
		
		HashMap<String,Integer> global = new HashMap<String,Integer>(functionDeclarations);
		variableDeclarations.forEach((key,value) -> global.merge(key, value, Integer::sum));
		global.forEach((key, value) -> { if(value > 1) duplicates.add(key); });
		
		return duplicates;
	}
	
	/* Returns true if there are identifiers declared more than 2 times, false otherwise
	 * @return boolean
	 */
	public boolean duplicateFree() {
		return getDuplicates().size() == 0;
	}

}