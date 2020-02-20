package environment;

import java.util.List;

public class BaseFunctionSTEntry extends BaseSTEntry {
	
	private List<ParameterEntry> parameters;
	
	public BaseFunctionSTEntry(String id, int nestingLevel, List<ParameterEntry> parameters) {
		super(id, nestingLevel);
		this.parameters = parameters;
	}

	public List<ParameterEntry> getParameters() {
		return parameters;
	}

	public String toString() {
		return "Symbol table entry: \n" +
				"\tnesting level: " + nestingLevel + "\n";
	}

}
