package com.onlinevotingsystem.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Admin extends BasicUser implements Serializable {
	
	/**
	 * Costruisce un admin vuoto
	 */
	public Admin() {
		
	}
	
	/**
	 * Costruisce un admin e gli assegna i permessi da funzionario comunale di default
	 * @param nickname		nome per il login
	 * @param password		password per il login, salvata criptata
	 */
	public Admin(String nickname, String password) {
		super(nickname, password, true);
	}
	
}
