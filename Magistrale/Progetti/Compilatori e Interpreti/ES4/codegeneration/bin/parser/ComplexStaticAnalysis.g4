grammar ComplexStaticAnalysis;

// PARSER RULES


block		: '{' statement* '}';

statement	: assignment ';'
		  	| deletion ';' 
		  	| print ';'
		  	| functioncall ';'
		  	| ifthenelse
		  	| declaration 
		  	| block ;

assignment	: ID '=' exp ;

deletion	: 'delete' ID ;

print		: 'print' exp ;

functioncall: ID '(' (exp (',' exp)* )? ')' ;

ifthenelse 	: 'if' '(' condition=exp ')' 'then' thenBranch=block 'else' elseBranch=block ;   

declaration	: type ID '=' exp ';' 									#variableDeclaration
		  	| ID '(' ( parameter ( ',' parameter)* )? ')' block 	#functionDeclaration;

type   		: 'int'  
        	| 'bool'  ;  

parameter  	: (var='var')? type ID ;

exp    		: (minus='-')? left=term (op=('+' | '-') right=exp)? ;
   
term   		: left=factor (op=('*' | '/') right=term)? ;
   
factor 		: left=value (rop=ROP right=value)?    				#relationalFactor
	        | left=value (bop=('&&' | '||') right=value)? 		#booleanFactor; 
   
value  		: INTEGER				#integerValue
     		| ( 'true' | 'false' )	#booleanValue
      		| '(' exp ')' 			#expValue
      		| ID  					#variableValue;
    
// LEXER RULES

ROP     : '==' | '>' | '<' | '<=' | '>=' | '!=' ;

//Numbers
fragment DIGIT : '0'..'9';    
INTEGER       : DIGIT+;

//IDs
fragment CHAR  : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPE SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip ;
LINECOMENTS    	: '//' (~('\n'|'\r'))* -> skip ;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip ;
ERR     	: .  -> channel(HIDDEN) ; 
