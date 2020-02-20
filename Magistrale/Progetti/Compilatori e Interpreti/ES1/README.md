# Esercitazione 1
## Gruppo
Berti Matteo - matteo.berti11@studio.unibo.it
Colledan Andrea - andrea.colledan@studio.unibo.it

## Descrizione
Creare una applicazione Java per SimpleStaticAnalysis che ritorna la lista degli errori lessicali in un file di input. La lista degli errori lessicali deve essere memorizzata in un file. Aggiungere anche una descrizione di come si integra nel progetto nel repository.

Il file .zip contiene il progetto per intero come caricato su IoL con già le modifiche apportate al fine di rilevare gli errori lessicali in un file di testo.

## Implementazione
Si sono usati due metodi diversi per l'analisi degli errori lessicali. Il primo semplice ed efficiente, il secondo utile se non si vuole andare a manipolare la grammatica.

### Analisi degli errori lessicali con token ERR
* Per prima cosa è stato aggiunto in fondo al file che descrive la grammatica *src/parser/Simple.g4* la seguente riga di codice:
```ERR : . -> channel(HIDDEN);``` 
Quello che fa è catalogare tutti i caratteri non conosciuti dalla grammatica in token di tipo ERR.

* Poi si è stato aggiunto all'interno del metodo main in *src/analyser/Analyse.java* il seguente blocco di codice:
```
Token token = lexer.nextToken();
while (token.getType() != SimpleLexer.EOF) {
	if(token.getType() == SimpleLexer.ERR)
		lexerErrors.add(token);
	token = lexer.nextToken();
}
```
Quello che fa è scorrere tutti i token creati dal lexer, riconoscere quelli di tipo ERR e aggiungerli ad una lista.

* Sempre in Analyse.java è stato aggiunto al posto del precedente controllo sugli errori semantici, il seguente blocco di codice:
```
if(lexerErrors.size() > 0) { // Token ERR found
	String dateTime = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n";
	
	System.out.println("\nLexical analysis FAILED: ERR tokens found.\nThere " + (lexerErrors.size() > 1 ? "are " + lexerErrors.size() + " errors" : "is 1 error") + ":");
	
	Writer fileWriter = new FileWriter("errors.txt");
	fileWriter.write(dateTime);
	for(Token err: lexerErrors) {
		String msg = "- Wrong character \"" + err.getText() + "\" in line " + err.getLine() + ", position " + err.getCharPositionInLine() + "\n";
		System.out.print(msg);
		fileWriter.write(msg);
	}
	fileWriter.close();
}
```
Che controlla se sono stati rilevati errori con questo sistema, poi stampa su un file la data e l'ora, poi tutta la lista di elementi non riconosciuti dal lexer indicando il carattere, la linea e la posizione.

### Analisi errori lessicali con ErrorListener
* Per utilizzare l'ErrorListener è necessario togliere la riga ```ERR : . -> channel(HIDDEN);``` eventualmente aggiunta in precedenza in *Simple.g4*. E commentare la parte relativa al punto precedente, quando si scorrono tutti i token, in quanto il token ERR non è più presente e darebbe errore.

* Per prima cosa si è creata una classe ErroListener all'interno del pacchetto analyser che estende la classe BaseErrorListener di ANTLR. In questo modo tramite il metodo *syntaxError()* ogni volta che viene rilevato un errore si aggiunge ad una lista.
```src/analyser/ErrorListener.java```

* Successivamente nell'Analyse.java è stato aggiunto il listener al lexer con il seguente codice, in modo che ne possa catturare gli errori lanciati dall'analisi lessicale.
```
ErrorListener errorListener = new ErrorListener();
lexer.removeErrorListeners();
lexer.addErrorListener(errorListener);
```

* Il controllo degli errori in caso di ErrorListener è il seguente:
```
if(errorListener.numErrors() > 0) { // ErrorListener caught errors
	String dateTime = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n";
	String msg = errorListener.returnErrors();
	
	System.out.println("\nLexical analysis FAILED: ErrorListener found errors.\nThere " + (errorListener.numErrors() > 1 ? "are " + errorListener.numErrors() + " errors" : "is 1 error") + ":\n" + msg);
	
	Writer fileWriter = new FileWriter("errors.txt");
	fileWriter.write(dateTime + msg);
	fileWriter.close();
}
```
Anche qui se vi sono errori nella lista degli errori vengono stampati oltre a data e ora tutti i caratteri non riconosciuti dal lexer.
