package util;

public enum RelationalOperator {
	EQUALS{
		@Override
		public String toString() {
			return "==";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			//use beq
			return "beq $t1 $a0 " + controlLabel + "\n"
					+ "li $a0 0\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 1\n"
					+ endLabel + ":\n";
		}
	},
	GREATER{
		@Override
		public String toString() {
			return ">";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			//x > y iff not x <= y
			return "bleq $t1 $a0 " + controlLabel + "\n"
					+ "li $a0 1\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 0\n"
					+ endLabel + ":\n";
		}
	},
	LESS{
		@Override
		public String toString() {
			return "<";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			//x < y iff not y <= x
			return "bleq $a0 $t1 " + controlLabel + "\n"
					+ "li $a0 1\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 0\n"
					+ endLabel + ":\n";
		}
	},
	LEQ{
		@Override
		public String toString() {
			return "<=";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			//use bleq
			return "bleq $t1 $a0 " + controlLabel + "\n"
					+ "li $a0 0\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 1\n"
					+ endLabel + ":\n";
		}
	},
	GEQ{
		@Override
		public String toString() {
			return ">=";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			//x >= y iff y <= x
			return "bleq $a0 $t1 " + controlLabel + "\n"
					+ "li $a0 0\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 1\n"
					+ endLabel + ":\n";
		}
	},
	NEQ{
		@Override
		public String toString() {
			return "!=";
		}
		@Override
		public String toWTMInstructions(String controlLabel, String endLabel) {
			// x != y iff not x == y
			return "beq $t1 $a0 " + controlLabel + "\n"
					+ "li $a0 1\n"
					+ "b " + endLabel + "\n"
					+ controlLabel + ":\n"
					+ "li $a0 0\n"
					+ endLabel + ":\n";
		}
	};
	
	public abstract String toWTMInstructions(String controlLabel, String endLabel);	
}
