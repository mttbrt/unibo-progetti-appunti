package environment;

public class BaseSTEntry {
	
	protected String id;
	protected int nestingLevel;
	
	public String getId() {
		return id;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	protected BaseSTEntry(String id, int nestingLevel) {
		this.id = id;
		this.nestingLevel = nestingLevel;
	}

	//We do not override hashCode and equals because two STE are the same when they are exactly the same object,
	//so == is sufficient to test for equality
	
	
	
	
}
