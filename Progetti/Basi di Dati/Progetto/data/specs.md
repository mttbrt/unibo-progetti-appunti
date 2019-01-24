#GIT-R specifications:
GIT-R è un gestore di versionamento tramite interfaccia web pensato per lo schema [gitflow](http://nvie.com/posts/a-successful-git-branching-model/).
L'utilizzo richiede una registrazione al sito.

##Un utente registrato può:
* **Creare un nuovo progetto**: alla creazione del progetto saranno creati nel sistema la repository relativa contenente una cartella per il branch master ed una per il branch develop. Secondo gitflow, nessun utente può direttamente svolegere modifiche del codice presente in master e develop. Di conseguenza, non esitono master privati e develop privati. Inseriamo un upload iniziale?
* **Chiedere l'associazione ad un nuovo progetto**: se un utente vuole collaborare ad un progetto di un altro può richiederne l'accesso alla repository. L'utente padre decide se garantirlo o meno. Una volta che l'utente ha avuto accesso, può modificare il codice e, perché questo sia integrato nel progetto, deve inviare una richiesta (pull request) all'utente padre, che può decidere di accettarla o rifiutarla.
* **Creare un feature branch**: unico modo di aggiungere nuove feature al progetto. L'utente deve inserire il nome (univoco) del branch che vuole creare e effettuare un fork. Una volta fatto, il file system conterrà una cartella pubblica con la versione comune del codice del branch, inizialmente copiandolo dal develop, e una cartella privata contenete il codice su cui l'utente sta lavorando. Sullo stesso feature branch possono lavorare più utenti: in questo caso, la cartella pubblica sarà una ma ogni utente avrà una cartella privata. Per passare da privato a pubblico si effettua un push, indicando al sistema di cambiare il puntatore pubblico verso la versione privata indicata.
* **Creare un hotfix branch**: vale lo stesso discorso del feature branch, ma anziché essere creato a partire dal develop, è creato a partire dal master.
* **Creare un release branch**: solo l'utente padre può creare un fork per le releases. A parte questo, funziona quanto detto per i feature branch. Quando un releae branch è pronto, si inserisce un tag univoco per progetto indicante la versione e si mergia il release branch nel master branch. Non può esistere contemporaneamente più di un release branch aperto, cioè con dei release branches privati in uso. Nel release branch possono essere presenti solo bugfix.
* **Effettuare un commit**: per caricare nuove versioni nella propria cartella privata. Il sistema cambia il puntatore dell'ultima versione del branch privato alla nuova cartella.
* **Effettuare una pull request**: l'utente suggerisce al padre della repo di utilizzare il suo codice nel develop. Se il padre accetta, il sistema cambia il puntatore all'interno del file system, puntando alla cartella pubblica del branch di chi ha effettuato la richiesta.

##Cosa succede quando:
* **Si effettua un hotfix**: il codice dell'hotfix è mergiato nel master e nel develop branch. È necessario specificare un nuovo numero di versione.
* **Esistono differenze nelle versioni**: l'utente padre ha potere di decidere riga per riga su cosa tenere e cosa scartare.
* **Si efettua un push in un release branch**: Ad ogni push verso la cartella pubblica del release branch, il codice va mergiato nel develop branch.
