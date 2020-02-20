package environment.compilation;

import java.util.List;

import environment.BaseFunctionSTEntry;
import environment.ParameterEntry;

public class CompilationFunctionSTEntry extends BaseFunctionSTEntry {
	private String label;

	public CompilationFunctionSTEntry(String id, int nestingLevel, List<ParameterEntry> parameters, String label) {
		super(id, nestingLevel, parameters);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
