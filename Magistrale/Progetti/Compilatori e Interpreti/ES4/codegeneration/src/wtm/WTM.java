package wtm;

import wtm.parser.WTMParser;

public class WTM {
	public static final int CODESIZE = 2500;
    public static final int MEMSIZE = 10000;
    
    public final static int REGNUM = 6;
	
	public final static int A0 = 0;
	public final static int T1 = 1;
	public final static int FP = 2;
	public final static int AL = 3;
	public final static int SP = 4;
	public final static int RA = 5;
	
	private int steps = 0;
 
    private WTMInstruction[] code;
    private int ip;
    
    private int[] memory;
    private int[] registers;
    
    public WTM(WTMInstruction[] code) {
    	//init code and instruction pointer
    	this.code = code;
    	ip = 0;
    	
    	//init empty memory
    	memory = new int[MEMSIZE];

    	//init registers
    	registers = new int[REGNUM];
    	registers[SP] = MEMSIZE - 1; //starts from end of memory, although this location is NEVER used (see push)
    	registers[FP] = MEMSIZE - 2; //points to the bottom-most usable memory location
    }
    
    public void execute() {
    	while(true) {
    		if (registers[SP] <= 0) {
    			System.out.println("Error: out of memory.");
    			return;
    		}
    		if (ip >= code.length) {
    			System.out.println("Halted.");
    			return;
    		}
    		
    		WTMInstruction instruction = code[ip];
    		
    		switch(instruction.instr) {
    			case WTMParser.PUSH:	//push $r1
    				push(registers[instruction.arg0]);
    				break;
    			case WTMParser.TOP:	//top $r1
    				registers[instruction.arg0] = top();
    				break;
    			case WTMParser.POP:	//pop
    				pop();
    				break;
    			case WTMParser.MOVE:	//move $r1 $r2
    				registers[instruction.arg0] = registers[instruction.arg1];
    				break;
    			case WTMParser.ADD:	//add $r1 $r2 $r3
    				registers[instruction.arg0] = registers[instruction.arg1] + registers[instruction.arg2];
    				break;
    			case WTMParser.ADDINT:	//add $r1 $r2 val
    				registers[instruction.arg0] = registers[instruction.arg1] + instruction.arg2;
    				break;
    			case WTMParser.SUB:	//sub $r1 $r2 $r3
    				registers[instruction.arg0] = registers[instruction.arg1] - registers[instruction.arg2];
    				break;
    			case WTMParser.MULT:	//mult $r1 $r2 $r3
    				registers[instruction.arg0] = registers[instruction.arg1] * registers[instruction.arg2];
    				break;
    			case WTMParser.DIV:	//div $r1 $r2 $r3
    				registers[instruction.arg0] = registers[instruction.arg1] / registers[instruction.arg2];
    				break;
    			case WTMParser.CHANGESIGN:	//csig $r1 $r2
    				registers[instruction.arg0] = -registers[instruction.arg1];
    				break;
    			case WTMParser.STOREW:	//sw $r1 offset($r2)
    				memory[instruction.arg1 + registers[instruction.arg2]] = registers[instruction.arg0];
    				break;
    			case WTMParser.LOADW:	//lw $r1 offset($r2)
    				registers[instruction.arg0] = memory[instruction.arg1 + registers[instruction.arg2]];
    				break;
    			case WTMParser.LOADINT:	//li $r1 val
    				registers[instruction.arg0] = instruction.arg1;
    				break;
    			case WTMParser.BRANCH:	//b address
    				ip = instruction.arg2-1;
    				break;
    			case WTMParser.BRANCHEQ:	//beq $r1 $r2 address
    				if (registers[instruction.arg0] == registers[instruction.arg1]) ip = instruction.arg2-1;
    				break;
    			case WTMParser.BRANCHLESSEQ:	//bleq $r1 $r2 address
    				if (registers[instruction.arg0] <= registers[instruction.arg1]) ip = instruction.arg2-1;
    				break;
    			case WTMParser.JUMPANDLINK:	//jal address
    				registers[RA] = ip+1;
    				ip = instruction.arg2-1;
    				break;
    			case WTMParser.JUMPREGISTER:	//jr $r1
    				ip = registers[instruction.arg0]-1;
    				break;
    			case WTMParser.PRINT:	//print $r1
    				System.out.println(registers[instruction.arg0]);
    				break;
    			case WTMParser.HALT:	//halt
    				System.out.println("Halted.");
    				return;
    		}
    		
    		ip++;
    		
    		//easter egg
    		steps++;
    		if(steps == 1000000000) System.out.println("WTM warning: Looks like it's turtles all the way down...");
    	}
    }
    
    private void push(int value) {
    	memory[--registers[SP]] = value;
    }
    
    private int top() {
    	return memory[registers[SP]];
    }
    
    private void pop() {
    	//Inefficient, consider ignoring check
    	registers[SP] = registers[SP]+1 > MEMSIZE ? MEMSIZE : registers[SP]+1;
    }

	@Override
	public String toString() {
		String s = "";
		for(int i = MEMSIZE - 1; i >= 9970; i--) {
			s += "[" + i + "]\t| " + memory[i] + " \t|";
			if(MEMSIZE - 1 - i <= REGNUM + 1)
				switch(MEMSIZE - i - 1) {
					case 0:
						s += "\t A0 = " + registers[A0];
						break;
					case 1:
						s += "\t T1 = " + registers[T1];
						break;
					case 2:
						s += "\t FP = " + registers[FP];
						break;
					case 3:
						s += "\t AL = " + registers[AL];
						break;
					case 4:
						s += "\t SP = " + registers[SP];
						break;
					case 5:
						s += "\t RA = " + registers[RA];
						break;
					case 7:
						s += "\t ip = " + (ip + 1);
						break;
				}
			s += "\n";
			
			if(i == registers[SP])
				s += "----------------- $sp\n";
		}
		
		return s;
	}
    
}
