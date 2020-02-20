package util;

public enum BooleanOperator {
	AND {
		@Override
		public String toString() {
			return "&&";
		}
		@Override
		public String getAbsorbingElement() {
			return "0";
		}
	},
	OR {
		@Override
		public String toString() {
			return "||";
		}
		@Override
		public String getAbsorbingElement() {
			return "1";
		}
	};

	public abstract String getAbsorbingElement();
}
