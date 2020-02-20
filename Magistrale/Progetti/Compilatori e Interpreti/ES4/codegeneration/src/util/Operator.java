package util;

public enum Operator {
	PLUS{
		@Override
		public String toString() {
			return "+";
		}
		@Override
		public String toWTMInstruction() {
			return "add";
		}
	},
	MINUS{
		@Override
		public String toString() {
			return "-";
		}
		@Override
		public String toWTMInstruction() {
			return "sub";
		}
	},
	TIMES{
		@Override
		public String toString() {
			return "*";
		}
		@Override
		public String toWTMInstruction() {
			return "mult";
		}
	},
	DIVIDE{
		@Override
		public String toString() {
			return "/";
		}
		@Override
		public String toWTMInstruction() {
			return "div";
		}
	};
	
	public abstract String toWTMInstruction();
}