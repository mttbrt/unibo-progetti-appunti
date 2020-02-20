package model;

import environment.Environment;
import environment.Type;
import util.BooleanOperator;

public class BooleanFactor extends Factor {
	
	private BooleanOperator bop;
	
	public BooleanFactor(Value left, BooleanOperator bop, Value right) {
		super(left, right);
		this.bop = bop;
	}

	public BooleanOperator getBop() {
		return bop;
	}
	
	@Override
	public Type checkType(Environment env) {
		Type leftType = this.left.checkType(env);

		if(this.right != null) {
			Type rightType = this.right.checkType(env);
			if(rightType == null) {
				return null;
			}
			if(leftType == rightType) {
				//Both sides must be BOOL
				if (leftType == Type.BOOL) {
					return Type.BOOL;
				} 
				System.out.println("Type error: operator \"" + bop + "\" expects type BOOL in expression \"" + this.toString() + "\".");
				return null;
			}
			//left and right are not of the same type or one side did not type correctly.
			System.out.println("Type error: type mismatch between \"" + left + "\" and \"" + right + "\" in expression \"" + this.toString() + "\".");
			return null;
		
		} 

		return leftType;
	}
	
	@Override
	public String toString() {
		if(right!=null)
			return left.toString() + bop + right.toString();
		else
			return left.toString();
	}
	
	@Override
	public String generateWTMCode(Environment env) {
		String code = left.generateWTMCode(env);
		if (right != null) {
			//use the fact that x && y = 0 if x==0, y otherwise (same with || and 1)
			String shortcircuitLabel = env.newLabel();
			code += "li $t1 " + bop.getAbsorbingElement() + "\n"
					+ "beq $t1 $a0 " + shortcircuitLabel + "\n"
					+ right.generateWTMCode(env)
					+ shortcircuitLabel + ":\n";
		}
		return code;
	}

}
