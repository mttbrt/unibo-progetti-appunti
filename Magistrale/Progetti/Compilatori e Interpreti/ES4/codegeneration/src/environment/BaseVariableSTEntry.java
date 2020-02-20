package environment;

public class BaseVariableSTEntry extends BaseSTEntry {
	
	private Type type;
	
	public BaseVariableSTEntry(String id, int nestingLevel, Type type) {
		super(id, nestingLevel);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	public String toString() {
		return "Symbol table entry: \n" +
				"\tnesting level: " + nestingLevel + "\n" +
				"\ttype: " + type.toString() + "\n";
	}
}
