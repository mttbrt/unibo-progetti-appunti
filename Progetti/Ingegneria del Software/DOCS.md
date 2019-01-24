# Documentazione

## 1. Client

### 1.1 GUI

#### 1.1.1 Appointment

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
public void setUsersList(ArrayList<User> users)
```
Imposta l'elenco degli utenti registrati al sito

 * **Parameters:** `users` — utenti registrati

```
#!java
private class AppointBTNClickHandler implements ClickHandler
```
Gestisce il click del button Appoint: nomina l'utente funzionario comunale

```
#!java
private class RevokeBTNClickHandler implements ClickHandler
```
Gestisce il click del button Revoke: revoca la nomina di funzionario comunale

#### 1.1.2 Approval

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
public void setListsList (ArrayList<List> lists)
```
Imposta l'elenco delle liste in attesa di approvazione

 * **Parameters:** `lists` — liste in attesa (pending)

```
#!java
private class ApproveBTNClickHandler implements ClickHandler
```
Gestisce il click del button Approve: cambia lo stato della lista

```
#!java
private class RejectBTNClickHandler implements ClickHandler
```
Gestisce il click del button Reject: cambia lo stato della lista

```
#!java
private class LoadBTNClickHandler implements ClickHandler
```
Gestisce il click del button Load: carica dal DB e mostra le proprietà aggiornate delle liste create

```
#!java
private class ClearBTNClickHandler implements ClickHandler
```
Pulisce il DB delle liste create

#### 1.1.3 ElectionSetup

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
private void resetAll()
```
Ripristina i valori di default

```
#!java
private class SaveBTNClickHandler implements ClickHandler
```
Crea e salva nel DB una nuova elezione

```
#!java
private class LoadBTNClickHandler implements ClickHandler
```
Gestisce il click del button Load: carica dal DB e mostra le proprietà aggiornate delle elezioni create

```
#!java
private class ClearBTNClickHandler implements ClickHandler
```
Pulisce il DB delle elezioni create

#### 1.1.4 Foreground

```
#!java
void onUpdateForeground(User sessionUser)
```
Aggiorna il foreground

 * **Parameters:** `sessionUser` — utente loggato

```
#!java
void updateInfoLabel(String txt)
```
Aggiorna la label delle info

 * **Parameters:** `txt` — testo da scrivere

```
#!java
void updateErrorLabel(String txt)
```
Aggiorna la label degli errori

 * **Parameters:** `txt` — testo da scrivere

```
#!java
void updateSuccessLabel(String txt)
```
Aggiorna la label dei successi

 * **Parameters:** `txt` — testo da scrivere

#### 1.1.5 ListSetup

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
private void resetAll()
```
Ripristina i valori di default

```
#!java
public void setElectionsList(ArrayList<Election> elections)
```
Imposta l'elenco delle elezioni create

 * **Parameters:** `elections` — elezioni create

```
#!java
public void setUsersList(ArrayList<User> users)
```
Imposta l'elenco degli utenti registrati

 * **Parameters:** `users` — utenti registrati

```
#!java
public void setSpecificUser(User mayor)
```
Crea e salva nel DB una nuova lista (associata al candidato sindaco)

 * **Parameters:** `mayor` — candidato al ruolo di sindaco

```
#!java
private void updateUnlistedUsers()
```
Aggiorna gli utenti non candidati ad una lista

```
#!java
private class SaveBTNClickHandler implements ClickHandler
```
Verifica se la lista che si vuole creare rispetta alcuni vincoli

```
#!java
private class ListClickHandler implements ClickHandler
```
Gestisce il click su una voce della listBox

#### 1.1.6 Login

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
private void resetAll()
```
Ripristina i valori di default

```
#!java
private class LoginBTNClickHandler implements ClickHandler
```
Gestisce il click del button Login: esegue l'accesso dell'utente

#### 1.1.7 Main

```
#!java
public void displayMsg(int code, String msg)
```
Richiama il metodo override nel foreground definito, rispetto al codice (tipo di messaggio) che gli viene passato come paramentro. Inoltra un messaggio da stampare nella label.

 * **Parameters:**
   * `code` — tipo di messaggio
   * `msg` — messaggio

```
#!java
public void loginUser(BasicUser user)
```
Permette l'accesso alle funzionalità relative all'utente che effettua il login

 * **Parameters:** `user` — utente generale

```
#!java
public void setUsersList(ArrayList<User> users)
```
Richiama il metodo relativo all'elenco degli utenti registrati

 * **Parameters:** `users` — utenti registrati

```
#!java
public void setElectionsList(ArrayList<Election> elections)
```
Richiama il metodo relativo all'elenco delle elezioni create

 * **Parameters:** `elections` — elezioni create

```
#!java
public void setSpecificUser(User user)
```
Richiama il metodo relativo al salvataggio della lista (associata al sindaco)

 * **Parameters:** `user` — candidato sindaco

```
#!java
public void setListsList(ArrayList<List> lists)
```
Richiama il metodo relativo all'elenco delle liste create in attesa di approvazione

 * **Parameters:** `lists` — liste in attesa

```
#!java
public void setSpecificList(List list)
```
Richiama il metodo relativo all'elenco dei candidati per una specifica lista

 * **Parameters:** `list` — lista

```
#!java
public void setResults(ArrayList<String> result)
```
Richiama il metodo relativo all'elenco dei risultati

 * **Parameters:** `result` — risultati elezioni

```
#!java
private void setupNavbar()
```
Imposta la barra di navigazione: insieme di buttons che esprimono le funzionalità a cui può accedere l'utente

```
#!java
private void setupRegistration()
```
Creazione/Aggiornamento Gui Registrazione

```
#!java
private void setupLogin()
```
Creazione/Aggiornamento Gui Login

```
#!java
private void setupLogout()
```
Logout utente e ripristino barra di navigazione di default

```
#!java
private void setupAppointment()
```
Creazione/Aggiornamento Gui Nomina

```
#!java
private void setupNewElection()
```
Creazione/Aggiornamento Gui Creazione elezione

```
#!java
private void setupNewList()
```
Creazione/Aggiornamento Gui Creazione lista

```
#!java
private void setupApproval()
```
Creazione/Aggiornamento Gui Approvazione lista

```
#!java
private void setupProfile()
```
Creazione/Aggiornamento Gui Profilo utente

```
#!java
private void setupVote()
```
Creazione/Aggiornamento Gui Votazione

```
#!java
private void setupResults()
```
Creazione/Aggiornamento Gui Risultati

```
#!java
private void setupForeground(Composite element)
```
Rimuove tutti gli elementi nel content panel e aggiunge l'elemento composto specificato come parametro

 * **Parameters:** `element` — insieme di elementi grafici

```
#!java
private class ButtonClickHandler implements ClickHandler
```
Gestisce il click dei buttons nella barra di navigazione: richiama il metodo setup relativo al button cliccato

#### 1.1.8 Profile

```
#!java
public void setListsList(ArrayList<List> listTot)
```
Imposta l'elenco delle liste relative all'utente loggato, permettendo di visualizzarne le proprietà

 * **Parameters:** `listTot` — insieme di liste

#### 1.1.9 Registration

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
private void resetAll()
```
Ripristina i valori di default

```
#!java
private class SaveBTNClickHandler implements ClickHandler
```
Gestisce il click del button Save: dopo una serie di controlli, permette la registrazione dell'utente base al sito

```
#!java
private class LoadBTNClickHandler implements ClickHandler
```
Gestisce il click del button Load: carica dal DB e mostra le proprietà aggiornate degli utenti registrati

```
#!java
private class ClearBTNClickHandler implements ClickHandler
```
Pulisce il DB degli utenti registrati

#### 1.1.10 Results

```
#!java
public void editResults(ArrayList<String> results)
```
Imposta l'elenco dei risultati

 * **Parameters:** `results` — risultati elezioni (winners)

#### 1.1.11 VoteSetup

```
#!java
private void resetLabels()
```
Pulisce il contenuto delle labels

```
#!java
private void resetAll()
```
Ripristina i valori di default

```
#!java
public void setElectionsList(ArrayList<Election> elections)
```
Imposta l'elenco delle elezioni create

 * **Parameters:** `elections` — elezioni create

```
#!java
public void setElectionLists(ArrayList<List> lists)
```
Imposta l'elenco delle liste approvate per l'elezione selezionata

 * **Parameters:** `lists` — insieme di liste relative all'elezione

```
#!java
public void setSelectedList(List list)
```
Imposta l'elenco dei candidati per la lista selezionata

 * **Parameters:** `list` — lista

```
#!java
private void updateElectionLists()
```
Aggiorna l'elenco delle liste

```
#!java
private void updateListCandidates()
```
Aggiorna l'elenco dei candidati

```
#!java
private void clearListData()
```
Pulisce l'elenco dei candidati

```
#!java
private class ElectionClickHandler implements ClickHandler
```
Gestisce il click su una voce della listBox Elezioni

```
#!java
private class ListClickHandler implements ClickHandler
```
Gestisce il click su una voce della listBox Liste

```
#!java
private class VoteBTNClickHandler implements ClickHandler
```
Gestisce il click del button Vote: crea e registra il voto

### 1.2 Model

#### 1.2.1 Admin

```
#!java
public Admin()
```

Costruisce un admin vuoto

```
#!java
public Admin(String nickname, String password)
```

Costruisce un admin e gli assegna i permessi da funzionario comunale di default

 * **Parameters:**
   * `nickname` — nome per il login
   * `password` — password per il login, salvata criptata

#### 1.2.2 BasicUser

```
#!java
public BasicUser()
```

Costruisce un utente base vuoto

```
#!java
public BasicUser(String nickname, String password, boolean municipalOfficial)
```

Costruisce un utente base

 * **Parameters:**
   * `nickname` — identificativo scelto per il login
   * `password` — password per il login
   * `municipalOfficial` — true se ha permessi da funzionario comunale, falso altrimenti

```
#!java
public String getNickname()
```

Restituisce il nickname dell'utente base

 * **Returns:** nickname dell'utente base

```
#!java
public void setNickname(String nickname)
```

Modifica il nickname dell'utente base

 * **Parameters:** `nickname` — nuovo nickname dell'utente base

```
#!java
public String getPassword()
```

Restituisce la password dell'utente base

 * **Returns:** password dell'utente base

```
#!java
public void setPassword(String password)
```

Modifica la password dell'utente base

 * **Parameters:** `password` — nuova password dell'utente base

```
#!java
public boolean isMunicipalOfficial()
```

Restituisce i permessi dell'utente base

 * **Returns:** true se l'utente è un funzionario comunale, false altrimenti

```
#!java
public void setMunicipalOfficial(boolean municipalOfficial)
```

Modifica i permessi dell'utente base

 * **Parameters:** `municipalOfficial` — true se l'utente è nominato funzionario comunale, false se revocato

#### 1.2.3 Document

```
#!java
public Document()
```
Costruisce un documento vuoto

```
#!java
public Document(String type, String number, String body, Date rDate, Date eDate)
```

Costruisce un documento

 * **Parameters:**
   * `type` — tipo del documento: passaporto o carta di identita
   * `number` — numero univoco del documento
   * `body` — organismo che ha rilasciato il documento
   * `rDate` — data di rilascio del documento
   * `eDate` — data di scadenza del documento

```
#!java
public String getType()
```

Restituisce il tipo del documento

 * **Returns:** String tipo del documento: passaporto o carta di identita

```
#!java
public void setType(String type)
```

Modifica il tipo del documento

 * **Parameters:** `type` — tipo del documento: passaporto o carta di identita

```
#!java
public String getNumber()
```

Restituisce il numero del documento

 * **Returns:** String numero del documento

```
#!java
public void setNumber(String number)
```

Modifica il numero del documento

 * **Parameters:** `number` — numero del documento

```
#!java
public String getBody()
```

Restituisce l'organismo che ha rilasciato il documento

 * **Returns:** organismo che ha rilasciato il documento

```
#!java
public void setBody(String body)
```

Modifica l'organismo che ha rilasciato il documento

 * **Parameters:** `body` — organismo che ha rilasciato il documento
```
#!java
public Date getRDate()
```

Restituisce la data di rilascio del documento

 * **Returns:** data di rilascio del documento

```
#!java
public void setRDate(Date rDate)
```

Modifica la data di rilascio del documento

 * **Parameters:** `rDate` — data di rilascio del documento

```
#!java
public Date getEDate()
```

Restituisce la data di scadenza del documento

 * **Returns:** data di scadenza del documento

```
#!java
public void setEDate(Date eDate)
```

Modifica la data di scadenza del documento

 * **Parameters:** `eDate` — data di scadenza del documento

```
#!java
@Override  public String toString()
```

Override del metodo toString

 * **Returns:** String contenente tipo, numero, organismo, data di rilascio e di scadenza del documento

```
#!java
@Override  public boolean equals(Object obj)
```

Override del metodo equals che confronta i documenti in base a:tipo, numero, organismo, data di rilascio e di scadenza

 * **Returns:** true se i due oggetti solo uguali, altrimenti false


#### 1.2.4 Election

```
#!java
public Election()
```

Costruisce una elezione vuota

```
#!java
public Election(String subject, Date startDateTime, Date endDateTime)
```

Costruisce una elezione

 * **Parameters:**
   * `subject` — Oggetto dell elezione
   * `startDateTime` — Data e ora di inizio dell elezione
   * `endDateTime` — Data e ora di fine dell elezione

```
#!java
public String getSubject()
```

Restituisce l'oggetto dell'elezione

 * **Returns:** String oggetto dell elezione

```
#!java
public void setSubject(String subject)
```

Modifica l'oggetto dell'elezione

 * **Parameters:** `subject` — Oggetto dell elezione

```
#!java
public Date getStartDateTime()
```

Restituisce Data e ora di inizio elezione

 * **Returns:** Data e ora di inizio elezione

```
#!java
public void setStartDateTime(Date startDateTime)
```

Modifica Data e ora di inizio elezione

 * **Parameters:** `startDateTime` — Data e ora di inizio elezione

```
#!java
public Date getEndDateTime()
```

Restituisce Data e ora di fine elezione

 * **Returns:** Data e ora di fine elezione

```
#!java
public void setEndDate(Date endDateTime)
```

Modifica Data e ora di fine elezione

 * **Parameters:** `endDateTime` — Data e ora di fine elezione
```
#!java
public void addListToElection(String listID)
```

Aggiunge una lista, tramite il suo ID, all elenco delle liste presentate per quella elezione

 * **Parameters:** `listID` — identificativo della lista

```
#!java
public void removeListFromElection(String listID)
```

Rimuove una lista, tramite il suo ID, dall elenco delle liste presentate per quella elezione

 * **Parameters:** `listID` — identificativo della lista

```
#!java
public ArrayList<String> getLists()
```

Restituisce l'elenco delle liste presentate per l'elezione

 * **Returns:** ArrayList<String> elenco liste presentate per l'elezione

```
#!java
@Override  public String toString()
```

Override del metodo toString

 * **Returns:** String contenente: oggetto dell'elezione,data e ora di inizio e di fine

```
#!java
@Override  public boolean equals(Object obj)
```

Override del metodo equals che confronta le elezioni tra loro per oggetto

 * **Returns:** true se i due oggetti solo uguali, altrimenti false

#### 1.2.5 List

```
#!java
public List()

```

Costruisce una lista vuota

```
#!java
public List(String name, String symbol, User mayor, ArrayList<User> members)
```

Costruisce una lista

 * **Parameters:**
   * `name` — nome della lista
   * `symbol` — descrizione del simbolo della lista
   * `mayor` — candidato sindaco della lista
   * `members` — membri della lista

```
#!java
public List(String id, String name, String symbol, User mayor, ArrayList<User> members)
```

Costruisce una lista con identificativo univoco

 * **Parameters:**
   * `id` — identificativo univoco
   * `name` — nome della lista
   * `symbol` — descrizione del simbolo della lista
   * `mayor` — candidato sindaco della lista
   * `members` — membri della lista

```
#!java
public String getId()
```

Restituisce l'identificativo univoco della lista

 * **Returns:** identificativo univoco

```
#!java
public void setId(String id)
```

Modifica l'identificativo univoco della lista

 * **Parameters:** `id` — identificativo univoco

```
#!java
public int getStatus()
```

Restituisce lo stato della lista: approvata, rigettata, in attesa

 * **Returns:** lista approvata, rigettata o in attesa

```
#!java
public void setStatus(int status)
```

Modifica lo stato della lista in approvata, rigettata, in attesa

 * **Parameters:** `status` — approvata, rigettata o in attesa

```
#!java
public String getName()
```

Restituisce il nome della lista

 * **Returns:** nome della lista

```
#!java
public void setName(String name)
```

Modifica il nome della lista

 * **Parameters:** `name` — nome della lista

```
#!java
public String getSymbol()
```

Restituisce il simbolo della lista

 * **Returns:** simbolo della lista

```
#!java
public void setSymbol(String symbol)
```

Modifica il simbolo della lista

 * **Parameters:** `symbol` — simbolo della lista

```
#!java
public User getMayor()
```

Restituisce il candidato sindaco della lista

 * **Returns:** candidato sindaco della lista

```
#!java
public void setMayor(User mayor)
```

Modifica il candidato sindaco della lista

 * **Parameters:** `mayor` — candidato sindaco della lista

```
#!java
public ArrayList<User> getMembers()
```

Restituisce l'elenco dei membri della lista

 * **Returns:** elenco dei membri della lista

```
#!java
public void setMembers(ArrayList<User> members)
```

Modifica l'elenco dei membri della lista

 * **Parameters:** `members` — elenco dei membri della lista

```
#!java
@Override  public String toString()
```

Override del metodo toString che restituisce identificativo, stato, nome, descrizione del simbolo, nickname del candidato sindaco e numero di membri

 * **Returns:** identificativo, stato, nome, descrizione del simbolo, nickname del candidato sindaco e numero di membri

```
#!java
@Override  public boolean equals(Object obj)
```

Override del metodo equals che confronta le liste in base all'identificativo univoco

 * **Returns:** true se uguali, false altrimenti

#### 1.2.6 User

```
#!java
public User()
```

Costruisce un utente vuoto

```
#!java
public User(String nickname, String name, String surname,String phone, String password, String email, String cf, String address, String type, String number, String body, Date rDate, Date eDate)
```

Costruisce un utente

 * **Parameters:**
   * `nickname` — nickname dell'utente per il login
   * `name` — nome reale dell'utente
   * `surname` — cognome reale dell'utente
   * `phone` — recapito telefonico dell'utente
   * `password` — password dell'utente per il login
   * `email` — email dell'utente
   * `cf` — codice fiscale dell'utente
   * `address` — indirizzo di residenza dell'utente
   * `type` — tipo di documento: passaporto o carta d'identità
   * `number` — numero del documento
   * `body` — organismo che ha rilasciato il documento
   * `rDate` — data di rilascio del documento
   * `eDate` — data di scadenza del documento

```
#!java
public String getName()
```

Restituisce il nome reale

 * **Returns:** nome reale

```
#!java
public void setName(String name)
```

Modifica il nome reale

 * **Parameters:** `name` — nome reale

```
#!java
public String getSurname()
```

Restituisce il cognome reale

 * **Returns:** cognome reale

```
#!java
public void setSurname(String surname)
```

Modifica il cognome reale

 * **Parameters:** `surname` — cognome reale

```
#!java
public String getPhone()
```

Restituisce il recapito telefonico

 * **Returns:** recapito telefonico

```
#!java
public void setPhone(String phone)
```

Modifica il recapito telefonico

 * **Parameters:** `phone` — recapito telefonico

```
#!java
public String getEmail()
```

Restituisce la mail

 * **Returns:** mail

```
#!java
public void setEmail(String email)
```

Modifica la mail

 * **Parameters:** `email` — mail

```
#!java
public String getCf()
```

Restituisce il codice fiscale

 * **Returns:** codice fiscale

```
#!java
public void setCf(String cf)
```

Modifica il codice fiscale

 * **Parameters:** `cf` — codice fiscale

```
#!java
public String getAddress()
```

Restituisce l'indirizzo di residenza

 * **Returns:** indirizzo di residenza

```
#!java
public void setAddress(String address)
```

Modifica l'indirizzo di residenza

 * **Parameters:** `address` — indirizzo di residenza

```
#!java
public Document getDocument()
```

Restituisce il documento dell'utente

 * **Returns:** documento dell'utente

```
#!java
public void setDocument(Document document)
```

Modifica il documento dell'utente

 * **Parameters:** `document` — documento dell'utente

```
#!java
public void addCreatedList(String listID)
```

Aggiunge all'elenco delle liste create una nuova lista, tramite il suo id

 * **Parameters:** `listID` — identificativo della nuova lista creata

```
#!java
public void removeCreatedList(String listID)
```

Rimuove all'elenco delle liste create una lista, tramite il suo id

 * **Parameters:** `listID` — identificativo della lista creata

```
#!java
public ArrayList<String> getCreatedLists()
```

Restituisce l'elenco delle liste create

 * **Returns:** elenco delle liste create

```
#!java
public void addUserToList(String listID)
```

Aggiunge all'utente una nuova lista a cui appartiene, tramite l'id

 * **Parameters:** `listID` — identificativo della nuova lista

```
#!java
public void removeUserFromList(String listID)
```

Rimuove dall'utente una lista a cui appartiene, tramite l'id

 * **Parameters:** `listID` — identificativo della lista

```
#!java
public void addVotedElection(String electionSubj)
```

Aggiunge all'elenco delle elezioni in cui l'utente ha votato una nuova elezione, tramite l'oggetto

 * **Parameters:** `electionSubj` — oggetto dell'elezione votata

```
#!java
public void removeVotedElection(String electionSubj)
```

Rimuove dall'elenco delle elezioni in cui l'utente ha votato un'elezione, tramite l'oggetto

 * **Parameters:** `electionSubj` — oggetto dell'elezione

```
#!java
public ArrayList<String> getVotedElections()
```

Restituisce l'elenco delle elezioni in cui l'utente ha già votato

 * **Returns:** elenco delle elezioni in cui l'utente ha già votato

```
#!java
@Override  public String toString()
```

Override del metodo toString che restituisce nickname, nome, cognome, recapito, password, email, CF, residenza, dati sul documento, eventuale stato di funzionario comunale, liste create e di appartenenza, elelzioni votate

 * **Returns:** nickname, nome, cognome, recapito, password, email, CF, residenza, dati sul documento, eventuale stato di funzionario comunale, liste create e di appartenenza, elelzioni votate

```
#!java
@Override  public boolean equals(Object obj)
```

Override del metodo equals che confronta gli utenti in base a nickname e codice fiscale

 * **Returns:** true se uguali, false altrimenti

#### 1.2.7 Utils
```
#!java
public static String MD5(String string)
```

Restituisce l' MD5 hash della stringa passata

 * **Parameters:** `string` — cosa si desidera mappare in hash
 * **Returns:** stringa dell MD5 hash

#### 1.2.8 Vote

```
#!java
public Vote()
```

Costruisce un voto vuoto

```
#!java
public Vote(String election, String list, String candidate)
```

Costruisce un voto

 * **Parameters:**
   * `election` — oggetto dell'elezione che riceve il voto
   * `list` — nome della lista votata
   * `candidate` — nickname del candidato votato

```
#!java
public String getId()
```

Restituisce l'identificativo univoco

 * **Returns:** identificativo univoco

```
#!java
public void setId(String id)
```

Modifica l'identificativo univoco

 * **Parameters:** `id` — identificativo univoco

```
#!java
public String getElection()
```

Restituisce l'oggetto dell'elezione votata

 * **Returns:** oggetto dell'elezione votata

```
#!java
public void setElection(String election)
```

Modifica l'oggetto dell'elezione votata

 * **Parameters:** `election` — oggetto dell'elezione votata

```
#!java
public String getList()
```

Restituisce la lista votata

 * **Returns:** lista votata

```
#!java
public void setList(String list)
```

Modifica la lista votata

 * **Parameters:** `list` — lista votata

```
#!java
public String getCandidate()
```

Restituisce il candidato votato

 * **Returns:** candidato votato

```
#!java
public void setCandidate(String candidate)
```

Modifica il candidato votato

 * **Parameters:** `candidate` — candidato votato

```
#!java
@Override  public String toString()
```

Override del metodo toString che restituisce identificativo, oggetto dell'elezione, nome della lista e del candidato votati

 * **Returns:** identificativo, oggetto dell'elezione, nome della lista e del candidato votati

```
#!java
@Override  public boolean equals(Object obj)
```

Override del metodo equals che confronta i voti in base all'identificativo univoco

 * **Returns:** true se uguali, false altrimenti

## 2. Server

```
#!java 
@Override  public void clearDBUsers()
```
Elimina tutti gli utenti salvati nel database

```
#!java 
@Override  public void clearDBUsers()
```
Elimina tutti gli utenti salvati nel database

```
#!java 
@Override  public void clearDBLists()
```
Elimina tutte le liste salvate nel database

```
#!java 
@Override  public void clearDBElections()
```
Elimina tutte le elezioni salvate nel database

```
#!java 
@Override  public void clearDBVotes()
```
Elimina tutti i voti salvati nel database

```
#!java 
@Override  public BasicUser login(String nickname, String password)
```
Effettua il login di un utente

 * **Parameters:**
   * `nickname` — nickname utente
   * `password` — password per il login
 * **Returns:** null o il BasicUser in caso di successo

```
#!java 
@Override  public boolean registerList(String electionSubject, String creatorNickname, List list)
```
Registra una lista nel database

 * **Parameters:**
   * `electionSubject` — oggetto dell'elezione
   * `creatorNickname` — nickname del creatore
   * `list` — lista da inserire
 * **Returns:** true in caso di successo nell'inserimento, false altrimenti

```
#!java 
@Override  public boolean registerElection(Election election)
```
Registra un'elezione nel database

 * **Parameters:** `election` — elezione da inserire
 * **Returns:** true in caso di successo nell'inserimento, false altrimenti

```
#!java 
@Override  public boolean registerUser(User user)
```
Registra un utente nel database

 * **Parameters:** `user` — utente da inserire
 * **Returns:** true in caso di successo nell'inserimento, false altrimenti

```
#!java 
@Override  public boolean registerVote(Vote vote, String voter)
```
Registra un voto nel database

 * **Parameters:**
   * `vote` — voto da inserire
   * `voter` — nickname dell'utente che ha effettuato il voto
 * **Returns:** true in caso di successo nell'inserimento, false altrimenti

```
#!java 
@Override  public boolean editUser(User user)
```
Modifica un utente salvato nel database

 * **Parameters:** `user` — utente da modificare
 * **Returns:** true in caso di successo della modifica, false altrimenti

```
#!java 
@Override  public boolean editElection(Election election)
```
Modifica un'elezione salvata nel database

 * **Parameters:** `election` — elezione da modificare
 * **Returns:** true in caso di successo della modifica, false altrimenti

```
#!java 
@Override  public boolean editList(List list)
```
Modifica una lista salvata nel database

 * **Parameters:** `list` — lista da modificare
 * **Returns:** true in caso di successo della modifica, false altrimenti

```
#!java 
@Override  public ArrayList<User> getUnlistedUsersInElection(String electionSubject)
```
Restituisce gli utenti che non fanno già parte di una lista all'interno di una certa elezione

 * **Parameters:** `electionSubject` — oggetto dell'elezione per cui si vuole filtrare
 * **Returns:** arraylist di utenti

```
#!java 
@Override  public ArrayList<List> getElectionLists(String electionSubject)
```
Restituisce tutte le liste associate ad una certa elezione specificata

 * **Parameters:** `electionSubject` — oggetto dell'elezione di cui si vogliono le liste
 * **Returns:** arraylist di liste

```
#!java 
@Override  public ArrayList<List> getPendingLists()
```
Restituisce tutte le liste in stato pending presenti nel sistema

 * **Returns:** arraylist di liste

```
#!java 
@Override  public ArrayList<User> getUsers()
```
Restituisce tutti gli utenti presenti nel sistema

 * **Returns:** arraylist di utenti

```
#!java 
@Override  public BasicUser getUser(String nickname)
```
Restituisce un utente specificandone il nickname

 * **Parameters:** `nickname` — nickname dell'utente che si desidera
 * **Returns:** basicuser relativo all'utente o null se non presente

```
#!java 
@Override  public String getUsersAsString()
```
Restituisce la lista di tutti gli utenti nel sistema sotto forma di stringa

 * **Returns:** stringa contenente gli utenti del sistema

```
#!java 
@Override  public ArrayList<Election> getElections()
```
Restituisce tutte le elezioni presenti nel sistema

 * **Returns:** arraylist di elezioni

```
#!java 
@Override  public Election getElection(String subject)
```
Restituisce un'elezione specificandone l'oggetto

 * **Parameters:** `subject` — l'oggetto dell'elezione richiesta
 * **Returns:** l'elezione specificata o null se non presente

```
#!java 
@Override  public String getElectionsAsString()
```
Restituisce tutte le elezioni presenti nel sistema sotto forma di stringa

 * **Returns:** stringa contenente tutte le elezioni

```
#!java 
@Override  public ArrayList<List> getLists()
```
Restituisce tutte le liste presenti nel sistema

 * **Returns:** arraylist di liste

```
#!java 
@Override  public List getList(String id)
```
Restituisce una lista specificandone l'id

 * **Parameters:** `id` — l'id della lista richiesta
 * **Returns:** la lista richiesta o null se non presente

```
#!java 
@Override  public String getListsAsString()
```
Restituisce le liste presenti nel sistema sotto forma di stringa

 * **Returns:** stringa contenente le liste

```
#!java 
private Vote getVote(String id)
```
Restituisce un'elezione specificandone l'oggetto

 * **Returns:** l'elezione specificata o null se non presente

```
#!java 
private ArrayList<Vote> getVotes()
```
Restituisce tutti i voti presenti nel sistema

 * **Returns:** arraylist di voti

```
#!java 
@Override  public String getVotesAsString()
```
Restituisce tutti voti presenti nel sistema sotto forma di stringa

 * **Returns:** stringa contenente tutti i voti

```
#!java 
@Override  public ArrayList<Election> getActiveElections()
```
Restituisce tutte le elezioni che sono in questo momento in corso

 * **Returns:** arraylist di elezioni

```
#!java 
@Override  public ArrayList<List> getUserCreatedLists(User profile)
```
Restituisce tutte le liste create da un particolare utente

 * **Parameters:** `profile` — l'utente di cui si vogliono le liste create
 * **Returns:** arraylist di liste

```
#!java 
@Override  public ArrayList<Election> getUpcomingElections()
```
Restituisce tutte le elezioni non ancora iniziate

 * **Returns:** arraylist di elezioni

```
#!java 
public ArrayList<Election> getConcludedElections()
```
Restituisce tutte le elezioni concluse

 * **Returns:** arraylist di elezioni

```
#!java 
@Override  public ArrayList<Election> getUnvotedElections(User profile)
```
Restituisce tutte le elezioni in cui un utente non ha ancora espresso il proprio voto

 * **Parameters:** `profile` — l'utente di cui si vogliono le elezioni non votate
 * **Returns:** arraylist di elezioni

```
#!java 
@Override  public ArrayList<String> getResults()
```
Restituisce i risultati di tutte le elezioni concluse, ordinando in modo decrescente per numero di voti le liste e i candidati di ogni lista

 * **Returns:** arraylist di risultati sotto forma di stringa
```
#!java 
private Date getToday()
```
Restituisce la data di oggi presente e modificabile nel file today.txt

 * **Returns:** oggetto Date rappresentante data e ora in corso

```
#!java 
private boolean userExists(User checkUser)
```
Restituisce la presenza o meno di un utente nel database, controllando sia nickname che codice fiscale

 * **Parameters:** `checkUser` — l'utente di cui si vuole verificare l'esistenza
 * **Returns:** true se l'elemento esiste nel database, false altrimenti

```
#!java 
private boolean electionExists(Election checkElection)
```
Restituisce la presenza o meno di un'elezione nel database in base all'oggetto

 * **Parameters:** `checkElection` — l'elezione di cui si vuole verificare l'esistenza
 * **Returns:** true se l'elemento esiste nel database, false altrimenti

```
#!java 
private boolean listExists(List checkList)
```
Restituisce la presenza o meno di una lista nel database in base all'id

 * **Parameters:** `checkList` — la lista di cui si vuole verificare l'esistenza
 * **Returns:** true se l'elemento esiste nel database, false altrimenti

```
#!java 
private boolean voteExists(Vote vote, String voter)
```
Restituisce la presenza o meno di un voto nel database in base all'utente votante

 * **Parameters:**
   * `vote` — oggetto voto
   * `voter` — nickname del votante
 * **Returns:** true se l'elemento esiste nel database, false altrimenti

```
#!java 
private void checkAdminInDB(DB db)
```
Controlla che l'admin sia presente nel database, in caso contrario lo aggiunge

 * **Parameters:** `db` — database

```
#!java 
private void checkActualDatetime()
```
Controlla che la data e l'ora presenti nel file today.txt siano valide, in caso contrario le riscrive

```
#!java 
private DB getDB()
```
Restituisce un oggetto database rappresentante la struttura mapDB

 * **Returns:** database

```
#!java 
private class VotedElection
```
Classe privata con la funzione di organizzare il conteggio di un elezione

```
#!java 
private class VotedList
```
Classe privata con la funzione di organizzare il conteggio delle liste

```
#!java 
private class VotedCandidate
```
Classe privata con la funzione di organizzare il conteggio dei candidati

## 3. JUnit Test

```
#!java 
private void setUpServer()

```
Setup del server

```
#!java 
@Test  public void test0RegisterUser()
```
Test per la registrazione di un nuovo utente

```
#!java 
@Test  public void test1GetUser()
```
Test per la lettura di un utente dal database

```
#!java 
@Test  public void test2Login()
```
Test per il login di un utente

```
#!java 
@Test  public void test3EditUser()
```
Test per la modifica di un utente

```
#!java 
@Test  public void test4RegisterElection()
```
Test per la registrazione di una nuova elezione

```
#!java 
@Test  public void test5GetElection()
```
Test per la lettura di un' elezione

```
#!java 
@Test  public void test6EditElection()
```
Test per la modifica di un'elezione

```
#!java 
@Test  public void test7RegisterList()
```
Test per la registrazione di una nuova lista

```
#!java 
@Test  public void test8EditList()
```
Test per la modifica di una lista

```
#!java 
@Test  public void test9RegisterVote()
```
Test per la registrazione di un voto
