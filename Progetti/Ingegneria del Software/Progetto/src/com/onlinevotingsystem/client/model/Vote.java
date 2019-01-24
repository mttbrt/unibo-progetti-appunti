package com.onlinevotingsystem.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Vote implements Serializable {

	private String id;
	private String election;
	private String list;
	private String candidate;
	
	/**
	 * Costruisce un voto vuoto
	 */
	public Vote() {
		
	}
	
	/**
	 * Costruisce un voto
	 * @param election		oggetto dell'elezione che riceve il voto
	 * @param list		nome della lista votata
	 * @param candidate		nickname del candidato votato
	 */
	public Vote(String election, String list, String candidate) {
		this.election = election;
		this.list = list;
		this.candidate = candidate;
	}

	/**
	 * Restituisce l'identificativo univoco
	 * @return		identificativo univoco
	 */
	public String getId() {
		return id;
	}

	/**
	 * Modifica l'identificativo univoco 
	 * @param id		identificativo univoco
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Restituisce l'oggetto dell'elezione votata
	 * @return		oggetto dell'elezione votata
	 */
	public String getElection() {
		return election;
	}

	/**
	 * Modifica l'oggetto dell'elezione votata
	 * @param election		oggetto dell'elezione votata
	 */
	public void setElection(String election) {
		this.election = election;
	}

	/**
	 * Restituisce la lista votata
	 * @return		lista votata
	 */
	public String getList() {
		return list;
	}

	/**
	 * Modifica la lista votata
	 * @param list		lista votata
	 */
	public void setList(String list) {
		this.list = list;
	}

	/**
	 * Restituisce il candidato votato
	 * @return		candidato votato
	 */
	public String getCandidate() {
		return candidate;
	}

	/**
	 * Modifica il candidato votato
	 * @param candidate		candidato votato
	 */
	public void setCandidate(String candidate) {
		this.candidate = candidate;
	}
	
	/**
	 * Override del metodo toString che restituisce
	 * identificativo, oggetto dell'elezione, nome della lista e del candidato votati
	 * @return 		identificativo, oggetto dell'elezione, nome della lista e del candidato votati
	 */
	@Override
	public String toString() {
		return id + " " + election + " " + list + " " + candidate;
	}
	
	/**
	 * Override del metodo equals che confronta i voti in base all'identificativo univoco
	 * @return		true se uguali, false altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vote && ((Vote) obj).getId().equals(id))
			return true;
		else
			return false;
	}
	
}
