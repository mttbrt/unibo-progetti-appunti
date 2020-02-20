package environment.behavior;

import environment.BaseSTEntry;

public class BehavioralSTEntry extends BaseSTEntry {
	
	protected BehavioralType behavioralType;
	
	public BehavioralSTEntry(String id, int nestingLevel, BehavioralType behavioralType) {
		super(id, nestingLevel);
		this.behavioralType = behavioralType;
	}
	
	public BehavioralSTEntry(String id, int nestingLevel) {
		super(id, nestingLevel);
		behavioralType = new BehavioralType();
	}
	
	public BehavioralType getBehavioralType() {
		return behavioralType;
	}
	
	public void setBehavioralType(BehavioralType behavioralType) {
		this.behavioralType = behavioralType;
	}

	public String toString() {
		return "Symbol table entry: \n" +
				"\tnesting level: " + nestingLevel + "\n";
	}

}
