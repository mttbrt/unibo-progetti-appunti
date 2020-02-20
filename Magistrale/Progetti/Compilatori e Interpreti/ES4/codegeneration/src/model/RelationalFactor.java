package model;

import environment.Environment;
import environment.Type;
import util.RelationalOperator;

public class RelationalFactor extends Factor {
	
	private RelationalOperator rop;
	
	public RelationalFactor(Value left, RelationalOperator rop, Value right) {
		super(left, right);
		this.rop = rop;
	}
	public RelationalOperator getRop() {
		return rop;
	}
	
	@Override
	public Type checkType(Environment env) {
		Type leftType = this.left.checkType(env);
		
		if(this.right != null) {
			Type rightType = right.checkType(env);
			if(rightType == null) {
				return null;
			}
			if(leftType == rightType) {
				if(leftType == Type.BOOL) {
					//If type is BOOL we need rop to be either == or !=
					if(rop == RelationalOperator.EQUALS || rop == RelationalOperator.NEQ) {
						return Type.BOOL;
					}
					System.out.println("Type error: operator \"" + rop + "\" expects type INT in expression \"" + this.toString() + "\".");
					return null;
				}
				//If type is INT then any relational operator goes
				return Type.BOOL;
			}
			//left and right are not of the same type or one side did not type correctly.
			System.out.println("Type error: type mismatch between \"" + left + "\" and \"" + right + "\" in expression \"" + this.toString() + "\".");
			return null;
		}
		return leftType;
	}
	
	@Override
	public String toString() {
		if (right!=null) {
			return left.toString() + rop + right.toString();
		} else {
			return left.toString();
		}
	}
	
	@Override
	public String generateWTMCode(Environment env) {
		String code = left.generateWTMCode(env);
		if (right != null) {
			code += "push $a0\n"
					+ right.generateWTMCode(env)
					+ "top $t1\n"
					+ "pop\n"
					+ rop.toWTMInstructions(env.newLabel(), env.newLabel());
		}
		return code;
	}

}
