package com.onlinevotingsystem.client.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class List implements Serializable {

	public static final int PENDING_STATUS = 0;
	public static final int APPROVED_STATUS = 1;
	public static final int REJECTED_STATUS = 2;
	
	private String id;
	private int status;
	private String name;
	private String symbol;
	private User mayor;
	private ArrayList<User> members;
	
	/**
	 * Costruisce una lista vuota
	 */
	public List() {
		
	}
	
	/**
	 * Costruisce una lista
	 * @param name		nome della lista
	 * @param symbol		descrizione del simbolo della lista
	 * @param mayor		candidato sindaco della lista
	 * @param members		membri della lista
	 */
	public List(String name, String symbol, User mayor, ArrayList<User> members) {
		this.id = "";
		this.status = PENDING_STATUS;
		this.name = name;
		this.symbol = symbol;
		this.mayor = mayor;
		this.members = members;
	}
	
	/**
	 * Costruisce una lista con identificativo univoco
	 * @param id		identificativo univoco
	 * @param name		nome della lista
	 * @param symbol		descrizione del simbolo della lista
	 * @param mayor		candidato sindaco della lista
	 * @param members		membri della lista
	 */
	public List(String id, String name, String symbol, User mayor, ArrayList<User> members) {
		this.id = id;
		this.status = PENDING_STATUS;
		this.name = name;
		this.symbol = symbol;
		this.mayor = mayor;
		this.members = members;
	}

	/**
	 * Restituisce l'identificativo univoco della lista
	 * @return		identificativo univoco
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Modifica l'identificativo univoco della lista
	 * @param id		identificativo univoco
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Restituisce lo stato della lista: approvata, rigettata, in attesa
	 * @return		lista approvata, rigettata o in attesa
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Modifica lo stato della lista in approvata, rigettata, in attesa
	 * @param status		approvata, rigettata o in attesa
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Restituisce il nome della lista
	 * @return		nome della lista
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Modifica il nome della lista
	 * @param name		nome della lista
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Restituisce il simbolo della lista
	 * @return		simbolo della lista
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * Modifica il simbolo della lista
	 * @param symbol		simbolo della lista
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Restituisce il candidato sindaco della lista
	 * @return		candidato sindaco della lista
	 */
	public User getMayor() {
		return mayor;
	}
	
	/**
	 * Modifica il candidato sindaco della lista
	 * @param mayor		candidato sindaco della lista
	 */
	public void setMayor(User mayor) {
		this.mayor = mayor;
	}
	
	/**
	 * Restituisce l'elenco dei membri della lista
	 * @return		elenco dei membri della lista
	 */
	public ArrayList<User> getMembers() {
		return members;
	}
	
	/**
	 * Modifica l'elenco dei membri della lista
	 * @param members		elenco dei membri della lista
	 */
	public void setMembers(ArrayList<User> members) {
		this.members = members;
	}
	
	/**
	 * Override del metodo toString che restituisce
	 * identificativo, stato, nome, descrizione del simbolo, nickname del candidato sindaco e numero di membri
	 * @return 		identificativo, stato, nome, descrizione del simbolo, nickname del candidato sindaco e numero di membri
	 */
	@Override
	public String toString() {
		return id + " " + status + " " + name + " " + symbol + " " + mayor.getNickname() + " " + members.size();
	}
	
	/**
	 * Override del metodo equals che confronta le liste in base all'identificativo univoco
	 * @return		true se uguali, false altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof List && ((List) obj).getId().equals(id))
			return true;
		else
			return false;
	}
	
}
