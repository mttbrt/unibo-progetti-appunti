grammar WTM;

@lexer::members {
	public boolean correct = true;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
assembly: (instruction)* ;

instruction:	PUSH register=REGISTER
				| TOP register=REGISTER 		     
	  			| POP		   
	  			| MOVE dest=REGISTER src=REGISTER 
	  			| ADD dest=REGISTER left=REGISTER right=REGISTER
	  			| ADDINT dest=REGISTER left=REGISTER value=NUMBER		    
	  			| SUB	dest=REGISTER left=REGISTER right=REGISTER
			 	| MULT dest=REGISTER left=REGISTER right=REGISTER	    
			 	| DIV dest=REGISTER left=REGISTER right=REGISTER
			 	| CHANGESIGN dest=REGISTER src=REGISTER	    
				| STOREW src=REGISTER offset=NUMBER LPAR dest=REGISTER RPAR
				| LOADW dest=REGISTER offset=NUMBER LPAR src=REGISTER RPAR   
				| LOADINT dest=REGISTER value=NUMBER     
				| label=LABEL COL     
				| BRANCH label=LABEL  
				| BRANCHEQ left=REGISTER right=REGISTER label=LABEL 
				| BRANCHLESSEQ left=REGISTER right=REGISTER label=LABEL 
				| JUMPANDLINK label=LABEL
				| JUMPREGISTER register=REGISTER     
				| PRINT register=REGISTER       
				| HALT;
 	 
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
 
PUSH  	 	: 'push' ; 	// push the contents register on the stack
TOP			: 'top' ;	// load the top of the stack in register
POP	 		: 'pop' ; 	// pop from stack
MOVE		: 'move' ; 	// copy the contents of register src to register dest
ADD	 		: 'add' ;  	// store left+right in register dest
ADDINT		: 'addi';	// store left+value in register dest
SUB	 		: 'sub' ;	// store left-right in register dest
MULT	 	: 'mult' ;  // store left*right in register dest
DIV	 		: 'div' ;	// store left/right in register dest (integer division)
CHANGESIGN	: 'csig' ;	// store -src in register dest
STOREW	 	: 'sw' ; 	// store the contents of register src in the memory location offset + dest
LOADW	 	: 'lw' ;	// load in register dest the contents of the memory location offset + src
LOADINT		: 'li' ;	// load in register dest the integer value
BRANCH	 	: 'b' ;		// jump to label
BRANCHEQ 	: 'beq' ;	// jump to label if left == right
BRANCHLESSEQ: 'bleq' ;	// jump to label if left <= right
JUMPANDLINK	: 'jal' ;	// store next instruction address in $ra and jump to label
JUMPREGISTER: 'jr' ; 	// jump to instruction pointed by register
PRINT	 	: 'print' ;	// print the contents of register
HALT	 	: 'halt' ;	// stop execution

LPAR	: '(';
RPAR	: ')';
COL	 	: ':' ;
LABEL	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9'|'_')* ;
NUMBER	: '0' | ('-')?(('1'..'9')('0'..'9')*) ;
REGISTER: '$' ('a0'|'t1'|'fp'|'al'|'sp'|'ra') ;

WHITESP  : ( '\t' | ' ' | '\r' | '\n' )+   -> channel(HIDDEN);

ERR   	 : . { System.err.println("Invalid char: "+ getText()); correct = false;} -> channel(HIDDEN); 

