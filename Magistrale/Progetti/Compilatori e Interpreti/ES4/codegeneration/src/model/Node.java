package model;

import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import util.SemanticError;

public abstract class Node {
	/*
	 * Methods for checking semantic errors, which are
	 * 	-occurrences of undeclared variables
	 * 	-occurrences of undeclared functions
	 * 	-multiple declarations of a variable in the same environment
	 * 	-mismatch between formal and actual parameters
	 * 	-type errors
	 * 	-access to deleted identifiers
	 */
	
	/*
	 * Checks that variables/functions have been declared before being used and that
	 * there are no multiple declarations of the same variable in any one environment.
	 * Also checks that formal and actual parameters are consistent (especially var cases)
	 */
	public abstract List<SemanticError> checkSemantics(Environment env, boolean allowDeletion);
	
	/*
	 * Checks typing constraints on the corresponding part of the program.
	 */
	public abstract Type checkType(Environment env);
	
	/*
	 * Checks that deleted  identifiers are not accessed.
	 */
	public abstract BehavioralType checkBehavioralType(Environment env);
	
	/*
	 * Returns a string containing the WTM code corresponding to the subtree rooted in the node.
	 */
	public abstract String generateWTMCode(Environment env);
}
