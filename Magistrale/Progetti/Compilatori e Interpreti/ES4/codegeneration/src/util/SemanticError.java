package util;

public class SemanticError {
	
	private final static int UNKNOWN = -1;
	
	public enum ErrorType {
		UNDECLARED_VAR,
		UNDECLARED_FUN,
		MULTIPLE_DECLARATION,
		PARAMETER_MISMATCH,
		REFERENCE_ERROR,
		SCOPE_ERROR,
		TYPE_ERROR,
		ACCESS_DELETED
	}
	
	ErrorType type;
	String description;
	
	int line;
	int column;
	
	public SemanticError(ErrorType type, String description, int line, int column) {
		this.type = type;
		this.description = description;
		this.line = line;
		this.column = column;
	}
	
	public SemanticError(ErrorType type, String description) {
		this(type, description, UNKNOWN, UNKNOWN);
	}
	
	@Override
	public String toString() {
		return description; //MIGHT CHANGE
	}
}
