package environment.behavior;

public class Consistency {
	
	private ConsistencyResult result;
	private String offendingId;
	
	public Consistency(ConsistencyResult result, String offendingId) {
		super();
		this.result = result;
		this.offendingId = offendingId;
	}
	
	public Consistency(ConsistencyResult result) {
		super();
		this.result = result;
	}

	public ConsistencyResult getResult() {
		return result;
	}

	public String getOffendingId() {
		return offendingId;
	}
	
}


