package com.onlinevotingsystem.client.gui;

import com.onlinevotingsystem.client.model.User;

public interface Foreground {
	/**
	 * Aggiorna il foreground
	 * @param sessionUser		utente loggato
	 */
	void onUpdateForeground(User sessionUser);
	
	/**
	 * Aggiorna la label delle info
	 * @param txt		testo da scrivere
	 */
	void updateInfoLabel(String txt);
	
	/**
	 * Aggiorna la label degli errori
	 * @param txt		testo da scrivere
	 */
	void updateErrorLabel(String txt);
	
	/**
	 * Aggiorna la label dei successi
	 * @param txt		testo da scrivere
	 */
	void updateSuccessLabel(String txt);
	
}
