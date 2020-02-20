package environment.compilation;

import environment.BaseVariableSTEntry;
import environment.Type;

public class CompilationVariableSTEntry extends BaseVariableSTEntry {
	
	private int offset;
	private boolean parameter;	//true iff variable is a formal parameter
	private boolean reference; //not null iff variable stores a reference to another variable
	
	public CompilationVariableSTEntry(String id, int nestingLevel, int offset, boolean parameter, boolean reference) {
		super(id, nestingLevel, Type.UNDEFINED);
		this.offset = offset;
		this.parameter = parameter;
		this.reference = reference;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isParameter() {
		return parameter;
	}

	public boolean isReference() {
		return reference;
	}
	
	
}
