package wtm;

/**
 * Represents a single WTM instruction. Integer instr denotes the type of operation, while arg0, arg1 and arg2
 * are the arguments (at most 3). Arguments appear in their natural order, except for instruction addresses,
 * which once translated from labels always occupy the third position (arg2), for ease of translation.
 * @author andco
 *
 */
public class WTMInstruction {
	private static final int EMPTY = 0;
	
	public int instr;
	public int arg0, arg1, arg2;
	
	public WTMInstruction(int instr, int arg0, int arg1, int arg2) {
		super();
		this.instr = instr;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	//Constructor for instructions such as: pop, halt, etc.
	public WTMInstruction(int instr) {
		this(instr, EMPTY, EMPTY, EMPTY);
	}
	
	//Constructor for instructions such as: push, top, etc.
	public WTMInstruction(int instr, int arg0) {
		this(instr, arg0, EMPTY, EMPTY);
	}
	
	//Constructor for instructions such as: move, li, etc.
	public WTMInstruction(int instr, int arg0, int arg1) {
		this(instr, arg0, arg1, EMPTY);
	}
		
}
