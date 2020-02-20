package environment;

import model.Parameter;

public class ParameterEntry {
	private boolean var;
	private Type type;
	private String id;
	
	public ParameterEntry(boolean var, Type type, String id) {
		this.var = var;
		this.type = type;
		this.id = id;
	}
	
	public ParameterEntry(Parameter parameterNode) {
		this.var = parameterNode.isVar();
		this.type = parameterNode.getType();
		this.id = parameterNode.getId();
	}

	public boolean isVar() {
		return var;
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}
	
}
