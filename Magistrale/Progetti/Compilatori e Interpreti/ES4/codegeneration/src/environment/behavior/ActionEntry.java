package environment.behavior;

import environment.BaseSTEntry;

public class ActionEntry {
	
	private ActionType actionType;
	private BaseSTEntry object;
	
	public ActionEntry(ActionType actionType, BaseSTEntry object) {
		this.actionType = actionType;
		this.object = object;
	}
	
	public ActionType getActionType() {
		return actionType;
	}
	
	public BaseSTEntry getObject() {
		return object;
	}
	
	public void setObject(BaseSTEntry object) {
		this.object = object;
	}
	
	public String getObjectId() {
		return object.getId();
	}

	@Override
	public String toString() {
		return actionType + "(" + object + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionEntry other = (ActionEntry) obj;
		if (actionType != other.actionType)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (object != other.object)
			return false;
		return true;
	}
	
	
	
	
	
}
