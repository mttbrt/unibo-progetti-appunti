package com.onlinevotingsystem.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("serial")
public class Election implements Serializable {
	
	private String subject;
	private Date startDateTime;
	private Date endDateTime;
	private ArrayList<String> lists;
	
	
	/**
	 * Costruisce una elezione vuota
	 */
	public Election() {
		
	}

	/**
	* Costruisce una elezione 
	*
	* @param subject		Oggetto dell elezione
	* @param startDateTime		Data e ora di inizio dell elezione
	* @param endDateTime		Data e ora di fine dell elezione
	*/
	public Election(String subject, Date startDateTime, Date endDateTime) {
		this.subject = subject;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		lists = new ArrayList<>();
	}

	/**
	 * Restituisce l'oggetto dell'elezione
	 * @return  		String oggetto dell elezione
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Modifica l'oggetto dell'elezione
	 * @param subject		Oggetto dell elezione
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Restituisce Data e ora di inizio elezione
	 * @return  		Data e ora di inizio elezione
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * Modifica Data e ora di inizio elezione
	 * @param startDateTime		Data e ora di inizio elezione
	 */
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * Restituisce Data e ora di fine elezione
	 * @return 		Data e ora di fine elezione
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * Modifica Data e ora di fine elezione
	 * @param endDateTime		Data e ora di fine elezione
	 */
	public void setEndDate(Date endDateTime) {
		this.endDateTime = endDateTime;
	}
		
	/**
	 * Aggiunge una lista, tramite il suo ID, all elenco delle liste presentate per quella elezione
	 * @param listID		identificativo della lista 
	 */
	public void addListToElection(String listID) {
		lists.add(listID);
	}
	
	/**
	 * Rimuove una lista, tramite il suo ID, dall elenco delle liste presentate per quella elezione
	 * @param listID		identificativo della lista 
	 */
	public void removeListFromElection(String listID) {
		lists.remove(listID);
	}
	
	/**
	 * Restituisce l'elenco delle liste presentate per l'elezione	
	 * @return 		ArrayList<String> elenco liste presentate per l'elezione	
	 */
	public ArrayList<String> getLists() {
		return lists;
	}
	
	/**
	 * 	Override del metodo toString 
	 * 	@return 	String contenente: oggetto dell'elezione,data e ora di inizio e di fine
	 */
	@Override
	public String toString() {
		return subject + ": " + startDateTime + " / " + endDateTime;
	}
	
	/**
	 * Override del metodo equals che confronta le elezioni tra loro per oggetto
	 * @return		true se i due oggetti solo uguali, altrimenti falso
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Election && ((Election) obj).getSubject().equals(subject))
			return true;
		else
			return false;
	}	
}