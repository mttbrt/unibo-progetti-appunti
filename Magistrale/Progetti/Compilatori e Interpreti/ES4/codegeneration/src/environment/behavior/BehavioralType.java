package environment.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import environment.BaseSTEntry;

public class BehavioralType {

	protected List<ActionEntry> actions; // Actions performed by typed entity
	
	private boolean tentative = false;
	
	//Empty type constructor
	public BehavioralType() {
		this.actions = new ArrayList<ActionEntry>();
	}
	
	//Copy constructor
	public BehavioralType(BehavioralType other) {
		this.actions = new ArrayList<ActionEntry>(other.getActions());
		this.tentative = other.isTentative();
	}
	
	public boolean addReadWrite(BaseSTEntry entry) {
		return actions.add(new ActionEntry(ActionType.RW, entry));
	}
	
	public boolean addDeclaration(BaseSTEntry entry) {
		return actions.add(new ActionEntry(ActionType.DEC, entry));
	}
	
	public boolean addRedeclaration(BaseSTEntry entry) {
		return actions.add(new ActionEntry(ActionType.RED, entry));
	}
	
	public boolean addDeletion(BaseSTEntry entry) {
		return actions.add(new ActionEntry(ActionType.DEL, entry));
	}
	
	public List<ActionEntry> getActions() {
		return actions;
	}

	//Attempts to incorporate target into current behavioral type. Fails if the two types are incompatible
	public Consistency incrementType(BehavioralType target) {
		this.tentative = this.tentative || target.isTentative();
		for(ActionEntry a : target.getActions()) {
			actions.add(a);
			Consistency consistency = this.isLastConsistent();
			if(consistency.getResult() != ConsistencyResult.OK) return consistency;
		}

		return new Consistency(ConsistencyResult.OK);
	}
	
	private Consistency isLastConsistent() {
		int last = actions.size() - 1;
		ActionEntry lastAction = actions.get(last);
		Consistency consistent = new Consistency(ConsistencyResult.OK);
		
		switch(lastAction.getActionType()) {
			case RED: {
				for(int i = last-1; i >= 0; i--) {
					if(actions.get(i).getActionType() == ActionType.DEL
							&& actions.get(i).getObjectId().equals(lastAction.getObjectId())) {
						//identifier has been deleted before redeclaration, is consistent
						return consistent;
					}
					else if((actions.get(i).getActionType() == ActionType.DEC
							|| actions.get(i).getActionType() == ActionType.RED)
							&& actions.get(i).getObjectId().equals(lastAction.getObjectId())) {
						//identifier is declared multiple times, is inconsistent
						return new Consistency(ConsistencyResult.DEC_RED, lastAction.getObjectId());
					}
				}
				//There MUST be a deletion in the SAME block
				/*This pessimistic approach is justified in function declarations, where parameters are pushed
				 * immediately upon declarations in the environment, but actual behavioral types are only updated
				 * at the end of the declaration.
				 */
				return new Consistency(ConsistencyResult.DEC_RED, lastAction.getObjectId());
			}
			case RW: {
				for(int i = last-1; i >= 0; i--) {
					if(actions.get(i).getActionType() == ActionType.DEL
							&& actions.get(i).getObject() == lastAction.getObject()) {
						//D/RW conflict, is inconsistent
						return new Consistency(ConsistencyResult.DEL_RW, lastAction.getObjectId());
					}		
					else if((actions.get(i).getActionType() == ActionType.DEC
							|| actions.get(i).getActionType() == ActionType.RED)
							&& actions.get(i).getObject() == lastAction.getObject()) {
						//uses a local variable, is immediately consistent
						return consistent;
					}
				}
				//There is no deletion in block, must refer to a declaration in an external scope
				//is consistent (for now)
				return consistent;
			}
			case DEL: {
				for(int i = last-1; i >= 0; i--) {
					if(actions.get(i).getActionType() == ActionType.DEL
							&& actions.get(i).getObject() == lastAction.getObject()) {
						//double deletion without redeclaration, is inconsistent
						return new Consistency(ConsistencyResult.DEL_DEL, lastAction.getObjectId());
					}
					else if((actions.get(i).getActionType() == ActionType.DEC
							|| actions.get(i).getActionType() == ActionType.RED)
							&& actions.get(i).getObject() == lastAction.getObject()) {
						//deletes a local variable, is immediately consistent
						return consistent;
					}
				}
				//There is no declaration of the same identifier in the local block, must refer to a variable in an external scope
				//is consistent (for now)
				return consistent;
			}
			default: {
				//First declaration of a variable is always consistent
				return consistent;
			}
		}
	}
	
	public void cleanType() {
		for(int i = 0; i < actions.size(); i++) {
			if(actions.get(i).getActionType() == ActionType.DEC || actions.get(i).getActionType() == ActionType.RED) {
				for(int j = i + 1; j < actions.size(); j++) {
					if(actions.get(j).getObject() == actions.get(i).getObject()) {
						actions.remove(j);
						j--;
					}
				}
				actions.remove(i);
				i--;
			}
		}
	}
	
	public boolean isITECompatible (BehavioralType other) {
		//We assume this and other to be consistent
		HashSet<ActionEntry> thisDeletions = new HashSet<>(actions); //get this's distinct, unordered actions
		HashSet<ActionEntry> otherDeletions = new HashSet<>(other.getActions()); //get other's distinct, unordered actions
		
		//Two types are compatible as then and else branches iff they read and delete the same objects
		return thisDeletions.equals(otherDeletions);
	}
	
	public boolean isTentative() {
		return tentative;
	}
	
	public void setTentative(boolean tentative) {
		this.tentative = tentative;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
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
		BehavioralType other = (BehavioralType) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String s = "{";
		if(!actions.isEmpty()) {
			for(ActionEntry a : actions) {
				s += a.getActionType().name() + "([" + a.getObjectId() + ": " + a.getObject().hashCode() + "]), ";
			}
			s = s.substring(0, s.length()-2);
		}
		s+= "}";
		return s;
	}
	
	

	
	
}
