package environment.behavior;

import java.util.ArrayList;
import java.util.List;

import environment.BaseSTEntry;

public class ParametricBehavioralType extends BehavioralType {

	private List<String> formalParameters;
		
	public ParametricBehavioralType() {
		super();
		this.formalParameters = new ArrayList<>();
	}
	
	public ParametricBehavioralType(ParametricBehavioralType other) {
		super(other);
		this.formalParameters = new ArrayList<>(other.getFormalParameters());
	}
	
	public BehavioralType instantiateType(List<BaseSTEntry> references) {
		//references and formalParameters have always the same size thanks to checkSemantics
		BehavioralType instancedType = new BehavioralType(this);
		
		for(int i = 0; i < references.size(); ++i) {
			//For every actual parameter
			if(references.get(i) != null) { //This ignores value arguments
				for(ActionEntry ae : actions) {
					if (ae.getObjectId().equals(formalParameters.get(i))) {
						ae.setObject(references.get(i));
					}
				}
			}
		}
		
		return instancedType;
	}

	public List<String> getFormalParameters() {
		return formalParameters;
	}
	
	public boolean addFormalParameter(String id) {
		return formalParameters.add(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((formalParameters == null) ? 0 : formalParameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParametricBehavioralType other = (ParametricBehavioralType) obj;
		if (formalParameters == null) {
			if (other.formalParameters != null)
				return false;
		} else if (!formalParameters.equals(other.formalParameters))
			return false;
		return true;
	}
	
	
}
