package environment;

public enum Type {
	INT {
		public String toString() {
			return "int";
		}
	}, 
	BOOL {
		public String toString() {
			return "bool";
		}
	},
	VOID {
		public String toString() {
			return "void";
		}
	},
	UNDEFINED {
		public String toString() {
			return "undefined";
		}
	}
}
