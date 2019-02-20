package com.onlinevotingsystem.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("serial")
public class User extends BasicUser implements Serializable {

	private String name;
	private String surname;
	private String phone;
	private String email;
	private String cf;
	private String address;
	private Document document;
	private ArrayList<String> createdLists; // Liste create dall'utente
	private ArrayList<String> joinedLists; // Liste di cui l'utente fa parte
	private ArrayList<String> votedElections; // Elezioni in cui l'utente ha votato
	
	/**
	 * Costruisce un utente vuoto
	 */
	public User() {

	}
	
	/**
	 * Costruisce un utente
	 * @param nickname		nickname dell'utente per il login		
	 * @param name		nome reale dell'utente
	 * @param surname		cognome reale dell'utente
	 * @param phone		recapito telefonico dell'utente
	 * @param password		password dell'utente per il login
	 * @param email		email dell'utente
	 * @param cf		codice fiscale dell'utente
	 * @param address		indirizzo di residenza dell'utente
	 * @param type		tipo di documento: passaporto o carta d'identità
	 * @param number		numero del documento 
	 * @param body		organismo che ha rilasciato il documento 
	 * @param rDate		data di rilascio del documento
	 * @param eDate		data di scadenza del documento
	 */
	public User(String nickname, String name, String surname,String phone, String password, String email, String cf, String address, String type, String number, String body, Date rDate, Date eDate) {
		super(nickname, password, false);
		this.name = name;
		this.surname = surname;
		this.phone = phone;
		this.email = email;
		this.cf = cf;
		this.address = address;
		this.document = new Document(type, number, body, rDate, eDate);
		createdLists = new ArrayList<>();
		joinedLists = new ArrayList<>();
		votedElections = new ArrayList<>();
	}

	/**
	 * Restituisce il nome reale
	 * @return 		nome reale
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica il nome reale
	 * @param name  		nome reale
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Restituisce il cognome reale
	 * @return		cognome reale
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Modifica il cognome reale
	 * @param surname  		cognome reale
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * Restituisce il recapito telefonico
	 * @return		recapito telefonico
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Modifica il recapito telefonico
	 * @param phone		recapito telefonico
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Restituisce la mail
	 * @return		mail
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Modifica la mail
	 * @param email		mail
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Restituisce il codice fiscale
	 * @return		codice fiscale
	 */
	public String getCf() {
		return cf;
	}

	/**
	 * Modifica il codice fiscale
	 * @param cf	codice fiscale
	 */
	public void setCf(String cf) {
		this.cf = cf;
	}

	/**
	 * Restituisce l'indirizzo di residenza
	 * @return		indirizzo di residenza
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Modifica l'indirizzo di residenza
	 * @param address		indirizzo di residenza
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Restituisce il documento dell'utente
	 * @return		documento dell'utente
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Modifica il documento dell'utente
	 * @param document		documento dell'utente
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * Aggiunge all'elenco delle liste create una nuova lista, tramite il suo id
	 * @param listID		identificativo della nuova lista creata
	 */
	public void addCreatedList(String listID) {
		createdLists.add(listID);
	}
	
	/**
	 * Rimuove all'elenco delle liste create una lista, tramite il suo id
	 * @param listID		identificativo della lista creata
	 */
	public void removeCreatedList(String listID) {
		createdLists.remove(listID);
	}
	
	/**
	 * Restituisce l'elenco delle liste create
	 * @return		elenco delle liste create
	 */
	public ArrayList<String> getCreatedLists(){
		return createdLists;
	}
	
	/**
	 * Aggiunge all'utente una nuova lista a cui appartiene, tramite l'id
	 * @param listID		identificativo della nuova lista
	 */
	public void addUserToList(String listID) {
		joinedLists.add(listID);
	}
	
	/**
	 * Rimuove dall'utente una lista a cui appartiene, tramite l'id
	 * @param listID		identificativo della lista
	 */
	public void removeUserFromList(String listID) {
		joinedLists.remove(listID);
	}
	
	/**
	 * Aggiunge all'elenco delle elezioni in cui l'utente ha votato una nuova elezione, tramite l'oggetto
	 * @param electionSubj		oggetto dell'elezione votata
	 */
	public void addVotedElection(String electionSubj) {
		votedElections.add(electionSubj);
	}
	
	/**
	 * Rimuove dall'elenco delle elezioni in cui l'utente ha votato un'elezione, tramite l'oggetto
	 * @param electionSubj		oggetto dell'elezione
	 */
	public void removeVotedElection(String electionSubj) {
		votedElections.remove(electionSubj);
	}
	
	/**
	 * Restituisce l'elenco delle elezioni in cui l'utente ha già votato
	 * @return		elenco delle elezioni in cui l'utente ha già votato
	 */
	public ArrayList<String> getVotedElections(){
		return votedElections;
	}
		
	/**
	 * Override del metodo toString che restituisce
	 * nickname, nome, cognome, recapito, password, email, CF, residenza, dati sul documento, eventuale stato di funzionario comunale, liste create e di appartenenza, elelzioni votate
	 * @return 		nickname, nome, cognome, recapito, password, email, CF, residenza, dati sul documento, eventuale stato di funzionario comunale, liste create e di appartenenza, elelzioni votate
	 */
	@Override
	public String toString() {
		return super.getNickname() + " " + name + " " + surname + " " + phone + " " + super.getPassword() + " " + email + " " + cf + " " + address + " [" + document.toString() + "] " + (super.isMunicipalOfficial() ? "MO" : "not MO" + " {" + createdLists + "} {" + joinedLists + "} {" + votedElections + "}");
	}
	
	/**
	 * Override del metodo equals che confronta gli utenti in base a nickname e codice fiscale
	 * @return		true se uguali, false altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User && ((User) obj).getNickname().equals(super.getNickname()) || ((User) obj).getCf().equals(cf))
				return true;
		else
			return false;
	}
	
}