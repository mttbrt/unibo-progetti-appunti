# Esercitazione 1
## Gruppo
Berti Matteo - matteo.berti11@studio.unibo.it
Colledan Andrea - andrea.colledan@studio.unibo.it

## Obiettivi
#### Esercizio I

Definire una sintassi per un linguaggio di programmazione che contiene la grammatica di SimpleStaticAnalysis e la estende con i seguenti costrutti:

* Tipi di dato Int e Bool.
* Dichiarazione di variabili. La portata di una variabile è dal punto in cui si trova in poi. Nello stesso blocco una variabile può essere ridefinita a patto che venga  cancellata prima. Una variabile può essere definita in un blocco più interno.
* Espressioni booleane.
* Estensione di statement con:
    * if-then-else.
    * definizioni di funzioni. La portata di una dichiarazione di funzione è da dove si trova in poi. Le regole di portata di una funzione sono le stesse di quelle delle variabili. 
    * le funzioni possono tornare soltanto void.
    * il passaggio dei parametri può essere "per valore" o "per riferimento" (usare la parola chiave "var").
* delete può cancellare qualunque identificatore, anche quelli di funzione. Il vincolo importante è che l'identificatore sia dichiarato nello stesso blocco. Con un'unica eccezione: la delete all'interno del corpo di una funzione può cancellare una variabile del chiamante se questa è passata per riferimento. [esempio f(var a) = delete a]

Ovviamente é possibile modificare una variabile del chiamante passata per riferimento.

#### Esercizio II

Scrivere un programma che conta il numero di dichiarazioni di funzioni in un programma.

#### Esercizio III

Scrivere un programma che ritorna "true" se non ci sono identificatori dichiarati 2 volte, "false" altrimenti. 

## Implementazione
Si è creato un nuovo progetto ANTLR, usando come file di partenza per la grammatica quello di SimpleStaticAnalysis. Il progetto porta il nome di `Extension` e la grammatica ad esso associata è `Extension.g4`.

#### Esercizio I

- I tipi di dato int e bool sono stati introdotti attraverso l'aggiunta di ```typeDeclaration```, che può essere ```int``` o ```bool```, a ```variableDeclaration```. Inoltre, sono stati aggiunti i valori di verità ```TRUE``` e ```FALSE``` come uniche produzioni del fragment ```TF```.

- Le espressioni booleane sono state definite in modo del tutto analogo alle espressioni aritmetiche (che per l'occasione sono state rinominate ```arithmExp```):

  ```
  boolExp		: '(' boolExp ')'
  			| 'NOT' boolExp
  			| left=boolExp op=('AND'|'OR'|'XOR') right=boolExp
  			| left=arithmExp ('=='|'!='|'>'|'<'|'>='|'<=') right=arithmExp
  			| left=boolExp ('=='|'!=') right=boolExp
  			| ID
  			| TF
  ```

  Un'spressione booleana può inoltre essere un confronto tra espressioni aritmetiche (con `==`,`!=`,`<`,`>`,`<=` o `>=`) o tra espressioni booleane (`==` o `!=`).

- La grammatica per l'if-then-else segue quella vista a lezione:

  ```
  ifStatement			: matchedIfStatement
  					| unmatchedIfStatement;
  
  matchedIfStatement	: 'if' boolExp 'then' matchedIfStatement 'else' 									matchedIfStatement
  					| simpleStatement;
  
  unmatchedIfStatement: 'if' boolExp 'then' statement
  					| 'if' boolExp 'then' matchedIfStatement 'else' 									unmatchedIfStatement;
  ```

   Con la necessaria aggiunta di `simpleStatement`, che corrisponde a un qualsiasi statement che *non* sia un if-then-else. A tal fine, la grammatica di `statement` è stata alterata come segue:

  ```
  statement		: ifStatement
  				| simpleStatement;
  			
  simpleStatement	:variableDeclaration ';'
  				| functionDeclaration
  				| assignment ';' 
  				| functionCall ';'
  				| deletion ';' 
  				| print ';'
  				| block;	
  ```

  Come è giusto che sia, ogni ramo else possibilmente ambiguo viene associato al ramo if più interno.

- La dichiarazione di funzione sono introdotte dal token `def`, seguito dall'identificatore di funzione e dai parametri formali tra parentesi tonde. Un parametro formale consiste nel token `val` o `var` (passaggio per valore o riferimento, rispettivamente), seguito da una `variableDeclaration`. Il corpo della funzione viene dato all'interno di un blocco.

#### Esercizii II e III

Il secondo e il terzo esercizio sono risolti con lo stesso metodo. Siccome le proprietà da decidere non sono "scoped", ma "globali", abbiamo deciso di non costruire un ulteriore albero come risultato della visita dell'AST, ma piuttosto di memorizzare informazioni sulle dichiarazioni durante la prima (ed unica) visita dello stesso, per poi verificare il numero di dichiarazioni di funzione e l'eventuale presenza di doppioni.

Si è realizzata una classe `Environment` in `model`, che contiene due hashtable: una per le dichiarazioni di variabile, una per le dichiarazioni di funzione. Ad ogni identificatore in tabella è associato il numero di volte che tale identificatore è stato dichiarato. Durante la visita dell'AST (vedi `ExtensionDeclarationVisitor.java`), ogniqualvolta viene visitata una dichiarazione, viene aggiunta un'occorrenza nella hashtable corrispondente.

```java
Environment globalEnvironment = new Environment();

@Override
public Environment visitVariableDeclaration(VariableDeclarationContext ctx) {
	String id = ctx.ID().getText();
	globalEnvironment.addVariableDeclaration(id);
	super.visitVariableDeclaration(ctx);
	return globalEnvironment;
}
	
@Override
public Environment visitFunctionDeclaration(FunctionDeclarationContext ctx) {
	String id = ctx.ID().getText();
	globalEnvironment.addFunctionDeclaration(id);
	super.visitFunctionDeclaration(ctx);
	return globalEnvironment;
}
```

Si noti come tali metodi modifichino imperativamente l'environment globale (che viene quindi popolato top-down), ma allo stesso tempo si noti come ciascun metodo (questo vale anche per i metodi relativi alle altre regole, che non alterano l'environment) restituisca l'environment globale. Questo valore di ritorno è chiaramente inutile nelle chiamate intermedie, in quanto l'environment non è popolato bottom-up, ma permette di ottenere un environment come risultato dell'invocazione del metodo `visit()` all'interno di `Analyzer.java`.

Il passo successivo consiste infatti nel, una volta ottenuto l'environment globale dalla visita dell'AST, consultare le proprietà di tale environment. La classe `Environment` mette a disposizione i metodi `getNumberOfFunctionDeclarations`, che conta le dichiarazioni di simboli di funzione (non necessariamente distinti) sommando i valori della rispettiva hashtable, e `duplicateFree`, che unisce le hashtable, produce una lista degli identificatori dichiarati *almeno* 2 volte e restituisce "true" se e solo se tale lista è vuota (ogni identificatore è dichiarato una sola volta).

In `Analyzer.java` viene invocata la visita partendo dal blocco radice. In seguito sono stampati a schermo i risultati degli esercizi II (il numero delle dichiarazioni di funzione) e III (se vi sono duplicati, e in tal caso quali simboli sono dichiarati più di una volta).

