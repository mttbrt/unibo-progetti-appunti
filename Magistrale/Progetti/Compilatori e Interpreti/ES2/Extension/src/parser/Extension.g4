grammar Extension;

// THIS IS THE PARSER INPUT 

block		: '{' statement* '}';

statement	: ifStatement
			| simpleStatement;
			
simpleStatement	:variableDeclaration ';'
				| functionDeclaration
				| assignment ';' 
				| functionCall ';'
				| deletion ';' 
				| print ';'
				| block;	
			
variableDeclaration : typeDeclaration ID ('=' exp)?;

functionDeclaration : 'def' ID '(' formalParameters ')' block;

formalParameters 	:formalParameter(','formalParameter)*
					| ;

formalParameter		: ('val' | 'var') variableDeclaration;

typeDeclaration : 'int' | 'bool';

assignment	: ID '=' exp;

functionCall: ID '('actualParameters')';

actualParameters	:actualParameter(','actualParameter)*
					| ; 
					
actualParameter	: (ID|NUMBER|TF);

deletion	: 'delete' ID;

print		: 'print' exp;

exp			: arithmExp | boolExp;

arithmExp		: '(' arithmExp ')'										#baseArithmeticExp
				| '-' arithmExp											#negArithmeticExp
				| left=arithmExp op=('*' | '/') right=arithmExp			#binArithmeticExp
				| left=arithmExp op=('+' | '-') right=arithmExp			#binArithmeticExp
				| ID 													#varArithmeticExp	
			    | NUMBER												#valArithmeticExp;
			    
boolExp		: '(' boolExp ')'												#baseBoolExp
			| 'NOT' boolExp													#negBoolExp
			| left=boolExp op=('AND'|'OR'|'XOR') right=boolExp				#binBoolExp
			| left=arithmExp ('=='|'!='|'>'|'<'|'>='|'<=') right=arithmExp	#arithmeticComparisonExp
			| left=boolExp ('=='|'!=') right=boolExp						#boolComparisonExp
			| ID															#varBoolExp
			| TF															#valBoolExp;

ifStatement	: matchedIfStatement
			| unmatchedIfStatement;

matchedIfStatement	: 'if' boolExp 'then' matchedIfStatement 'else' matchedIfStatement
					| simpleStatement;

unmatchedIfStatement 	: 'if' boolExp 'then' statement
						| 'if' boolExp 'then' matchedIfStatement 'else' unmatchedIfStatement;
						
// THIS IS THE LEXER INPUT

//IDs
fragment CHAR 	: 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//Numbers
fragment DIGIT	: '0'..'9';	
NUMBER          : DIGIT+;

//Truth values
TF		: 'TRUE' | 'FALSE';

//ESCAPE SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip;
LINECOMMENTS 	: '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMMENTS   : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMMENTS)* '*/' -> skip;
ERR				: . -> channel(HIDDEN);
