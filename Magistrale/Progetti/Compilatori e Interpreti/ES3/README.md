# Esercitazione 3
## Gruppo
Colledan Andrea - andrea.colledan@studio.unibo.it
Berti Matteo - matteo.berti11@studio.unibo.it

## Obiettivi
#### Esercizio I
Implementare un sistema di analisi:
* di variabili/funzioni non dichiarate
* di variabili dichiarate più volte nello stesso ambiente (in questa analisi non è corretto il codice ```{ int x = 4; delete x; int x = 5; }```)
* parametri attuali non conformi ai parametri formali (inclusa la verifica sui parametri passati per var)
* della correttezza dei tipi

#### Esercizio II
Controllare gli accessi a identificatori "cancellati".
In questo esercizio è possibile cancellare una variabile di un ambiente statico esterno. Ad esempio è possibile fare ```{ int x =1; {int y = x; delete x;} }```.
1. il codice ```int x = 4; delete x; int x = 5;``` deve essere considerato corretto.
2. Funzioni problematiche:
    -- ```f(var int x, var int y) { delete x; delete y; } ```
    -- ```f(var int x, int y) { if (y== 0) delete x; else x=x+y; } ```
    -- ```f(var int x, var int y) { int z = x; delete x; y = y+z; }```
    -- ```g(var int x, var int y) { delete x; delete y;} f(var int z){ g(z,z); }```

## Implementazione
La scelta sull'esercizio da eseguire viene presa a runtime, digitando come input il numero dell'esercizio. L'esercizio II utilizza in parte la logica dell'esercizio I.

#### Esercizio I
**Environment**
La classe ```Environment.java``` gestisce due symbol tables, una per le variabili ed una per le funzioni, entrambe costruite come liste di hashmap. Gli oggetti memorizzati sono di tipo ```SymblolTableEntry```, questa classe viene estesa da ```SimpleEntry``` la quale si dirama in ```SimpleVariableEntry``` e ```SimpleFunctionEntry```.
```SimpleVariableEntry``` memorizza, oltre a id e livello di annidamento, presenti in ```SymbolTableEntry``` anche il tipo della variabile. ```SimpleFunctionEntry``` invece memorizza anche la lista dei parametri della funzione (```ParameterEntry```), oltre ad id e livello di annidamento. È presente anche una lista chiamata ```parameterBuffer``` nella quale vengono inserite le variabili dichiarate nella firma di funzione, che saranno poi inserite nella symbol table all'apertura del blocco successico, che si riferirà sempre al corpo della funzione.
**Nodi**
In questo esercizio ogni nodo dell'AST implementa due metodi fondamentali: ```checkSemantics()``` per l'analisi semantica, restituisce una lista di ```SemanticError``` e ```checkType()``` per l'analisi del tipaggio che restituisce un oggetto ```Type```.
L'**assegnamento** si accerta che l'id sia presente all'interno della symbol table ed effettua ricorsivamente il controllo semantico anche sull'espressione assegnata. Per il tipaggio controlla che il tipo della variabile e il tipo effettivo dell'espressione combacino, poi ritorna il tipo dell'assegnamento.
La **cancellazione** controlla la presenza dell'id nella symbol table e come tipo ritorna VOID.
La **print** di fatto ritorna il controllo semantico della propria espressione e come tipo ha VOID.
L'**if-then-else** effettua un controllo ricorsivo sulla condizione, sul blocco then e sul blocco else, uguale per il tipaggio.
La **chiamata di funzione** controlla che la funzione sia stata dichiarata all'interno di uno scope minore o uguale a quello corrente ed effettua il controllo semantico sui parametri attuali, confrontandoli con quelli formali. Il tipaggio avviene anch'esso controllando che combacino i tipi dei parametri.
La **dichiarazione di funzione** per prima cosa bufferizza i parametri, poi effettua il controllo semantico sul corpo della funzione.
La **dichiarazione di una variabile** controlla che l'id sia presente nella symbol table e controlla l'espressione, il tipaggio si assicura che il tipo dichiarato e quello dell'espressione combacino.
Un **blocco** controlla ricorsivamente tutti i propri statemente, sia semanticamente che per quanto riguarda il tipaggio.

#### Esercizio II
**Environment**
In questo esercizio si è esteso l'```Environment```. Entra in gioco la classe ```Behavioral Entry``` che estende ```SymbolTableEntry```, questa contiene l'effettivo tipo comportamentale di una funzione o di un entry usando la classe ```BehavioralType```. La quale salva due liste di ```SymbolTableEntry```: staticDelete e staticReadWrite, usate rispettivamente per memorizzare le entry globali cancellate e lette o scritte. Contiene inotlre un campo tentative utilizzato ad esempio per l'analisi di funzioni ricorsive, quando il tipo comportamente non è definito fino a che non si effettua un secondo controllo. ```BehavioralType``` è estero da ```ParametricBehavioralType```, utilizzata per le funzioni, che aggiunge le liste formalDelete, formalReadWrite e formalParameters per indicare rispettivamente le cancellazioni e le letture o scritture all'interno di un blocco di funzione e i parametri formali della funzione. Con questi elementi nell'analisi comportamentale, tramite il metodo ```checkBehavioralType()```, viene controllato il tipo comportamentale tenendo conto quindi di cosa viene letto/scritto in rapporto a cosa viene cancellato.
**Nodi**

