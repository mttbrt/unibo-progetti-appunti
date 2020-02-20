package wtm.parser;

import java.util.HashMap;

import wtm.WTM;
import wtm.WTMInstruction;
import wtm.parser.WTMParser.AssemblyContext;
import wtm.parser.WTMParser.InstructionContext;

public class WTMProcessingVisitor extends WTMBaseVisitor<Void> {

	public WTMInstruction[] code = new WTMInstruction[WTM.CODESIZE];    
    private int ip = 0;
    private HashMap<String,Integer> labelAddresses = new HashMap<String,Integer>();
    private HashMap<Integer,String> labelReferences = new HashMap<Integer,String>();
    
	@Override
	public Void visitAssembly(AssemblyContext ctx) {
		visitChildren(ctx);
    	for (Integer referenceAddress: labelReferences.keySet()) {
            code[referenceAddress].arg2 = labelAddresses.get(labelReferences.get(referenceAddress));
    	}
    	code[ip] = new WTMInstruction(WTMParser.HALT); //Add a cautionary halt
    	return null;
	}

	@Override
	public Void visitInstruction(InstructionContext ctx) {
		int type = ctx.getStart().getType();
		switch(type) {
			case WTMParser.PUSH:	//push $r1
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.register.getText()));
				break;
			case WTMParser.TOP:	//top $r1
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.register.getText()));
				break;
			case WTMParser.POP:	//pop
				code[ip++] = new WTMInstruction(type);
				break;
			case WTMParser.MOVE:	//move $r1 $r2
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.src.getText()));
				break;
			case WTMParser.ADD:	//add $r1 $r2 $r3
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText()));
				break;
			case WTMParser.ADDINT:	//add $r1 $r2 val
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.left.getText()), Integer.parseInt(ctx.value.getText()));
				break;
			case WTMParser.SUB:	//sub $r1 $r2 $r3
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText()));
				break;
			case WTMParser.MULT:	//mult $r1 $r2 $r3
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText()));
				break;
			case WTMParser.DIV:	//div $r1 $r2 $r3
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText()));
				break;
			case WTMParser.CHANGESIGN:	//csig $r1 $r2
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), tokenizeRegister(ctx.src.getText()));
				break;
			case WTMParser.STOREW:	//sw $r1 offset($r2)
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.src.getText()), Integer.parseInt(ctx.offset.getText()), tokenizeRegister(ctx.dest.getText()));
				break;
			case WTMParser.LOADW:	//lw $r1 offset($r2)
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), Integer.parseInt(ctx.offset.getText()), tokenizeRegister(ctx.src.getText()));
				break;
			case WTMParser.LOADINT:	//li $r1 val
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.dest.getText()), Integer.parseInt(ctx.value.getText()));
				break;
			case WTMParser.LABEL:	//label:
				labelAddresses.put(ctx.label.getText(), ip);
				break;
			case WTMParser.BRANCH:	//b label
				labelReferences.put(ip, ctx.label.getText());
				code[ip++] = new WTMInstruction(type); //might have to add label
				break;
			case WTMParser.BRANCHEQ:	//beq $r1 $r2 label
				labelReferences.put(ip, ctx.label.getText());
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText())); //might have to add label
				break;
			case WTMParser.BRANCHLESSEQ:	//bleq $r1 $r2 label
				labelReferences.put(ip, ctx.label.getText());
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.left.getText()), tokenizeRegister(ctx.right.getText())); //might have to add label
				break;
			case WTMParser.JUMPANDLINK:	//jal label
				labelReferences.put(ip, ctx.label.getText());
				code[ip++] = new WTMInstruction(type); //might have to add label
				break;
			case WTMParser.JUMPREGISTER:	//jr $r1
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.register.getText()));
				break;
			case WTMParser.PRINT:	//print $r1
				code[ip++] = new WTMInstruction(type, tokenizeRegister(ctx.register.getText()));
				break;
			case WTMParser.HALT:	//halt
				code[ip++] = new WTMInstruction(type);
				break;
		}
		
		return null;
	}
	
	private int tokenizeRegister(String regName) {
		if (regName.equals("$a0")) {
			return WTM.A0;
		} else if (regName.equals("$t1")) {
			return WTM.T1;
		} else if (regName.equals("$fp")) {
			return WTM.FP;
		} else if (regName.equals("$al")) {
			return WTM.AL;
		} else if (regName.equals("$sp")) {
			return WTM.SP;
		} else if (regName.equals("$ra")) {
			return WTM.RA;
		} else {
			return -1;
		}
	}

}
