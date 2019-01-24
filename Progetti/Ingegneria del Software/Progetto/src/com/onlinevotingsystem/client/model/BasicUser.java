package com.onlinevotingsystem.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BasicUser implements Serializable {
	
	private String nickname;
	private String password;
	private boolean municipalOfficial;
	
	/**
	 * Costruisce un utente base vuoto
	 */
	public BasicUser() {

	}
	
	/**
	 * Costruisce un utente base 
	 * @param nickname		identificativo scelto per il login
	 * @param password		password per il login
	 * @param municipalOfficial			true se ha permessi da funzionario comunale, falso altrimenti
	 */
	public BasicUser(String nickname, String password, boolean municipalOfficial) {
		this.nickname = nickname;
		this.password = Utils.MD5(password);
		this.municipalOfficial = municipalOfficial;
	}
	
	/**
	 * Restituisce il nickname dell'utente base
	 * @return		nickname dell'utente base
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Modifica il nickname dell'utente base
	 * @param nickname		nuovo nickname dell'utente base
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * Restituisce la password dell'utente base
	 * @return		password dell'utente base
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifica la password dell'utente base
	 * @param password		nuova password dell'utente base
	 */
	public void setPassword(String password) {
		this.password = Utils.MD5(password);
	}

	/**
	 * Restituisce i permessi dell'utente base
	 * @return		true se l'utente è un funzionario comunale, false altrimenti
	 */
	public boolean isMunicipalOfficial() {
		return municipalOfficial;
	}

	/**
	 * Modifica i permessi dell'utente base
	 * @param municipalOfficial		true se l'utente è nominato funzionario comunale, false se revocato
	 */
	public void setMunicipalOfficial(boolean municipalOfficial) {
		this.municipalOfficial = municipalOfficial;
	}
	
}
