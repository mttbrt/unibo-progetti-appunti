package com.onlinevotingsystem.client.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Document implements Serializable {

	private String type;
	private String number;
	private String body;
	private Date rDate;
	private Date eDate;
	
	/**
	 * Costruisce un documento vuoto
	 */
	public Document() {

	}
	
	/**
	 * Costruisce un documento
	 * @param type		tipo del documento: passaporto o carta di identita
	 * @param number		numero univoco del documento
	 * @param body		organismo che ha rilasciato il documento
	 * @param rDate		data di rilascio del documento
	 * @param eDate		data di scadenza del documento
	 */
	public Document(String type, String number, String body, Date rDate, Date eDate) {
		this.type = type;
		this.number = number;
		this.body = body;
		this.rDate = rDate;
		this.eDate = eDate;
	}

	/**
	 * Restituisce il tipo del documento
	 * @return	String tipo del documento: passaporto o carta di identita
	 */
	public String getType() {
		return type;
	}

	/**
	 * Modifica il tipo del documento
	 * @param type	tipo del documento: passaporto o carta di identita
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Restituisce il numero del documento
	 * @return	String numero del documento
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Modifica il numero del documento
	 * @param number	numero del documento
	 */	
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Restituisce l'organismo che ha rilasciato il documento
	 * @return		organismo che ha rilasciato il documento
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Modifica l'organismo che ha rilasciato il documento
	 * @param body	organismo che ha rilasciato il documento
	 */	
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Restituisce la data di rilascio del documento
	 * @return 		data di rilascio del documento
	 */
	public Date getRDate() {
		return rDate;
	}
	/**
	 * Modifica la data di rilascio del documento
	 * @param rDate		data di rilascio del documento
	 */
	public void setRDate(Date rDate) {
		this.rDate = rDate;
	}

	/**
	 * Restituisce la data di scadenza del documento
	 * @return 		data di scadenza del documento
	 */
	public Date getEDate() {
		return eDate;
	}

	/**
	 * Modifica la data di scadenza del documento
	 * @param eDate		data di scadenza del documento
	 */
	public void setEDate(Date eDate) {
		this.eDate = eDate;
	}

	/**
	 * 	Override del metodo toString 
	 * 	@return 	String contenente tipo, numero, organismo, data di rilascio e di scadenza del documento
	 */
	@Override
	public String toString() {
		return type + " " + number + " " + body + " " + rDate + " " + eDate;
	} 
	
	/**
	 * Override del metodo equals che confronta i documenti in base a:tipo, numero, organismo, data di rilascio e di scadenza
	 * @return		true se i due oggetti solo uguali, altrimenti falso
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Document && ((Document) obj).getType().equals(type) && ((Document) obj).getNumber().equals(number) &&
				((Document) obj).getBody().equals(body) && ((Document) obj).getRDate().equals(rDate) && ((Document) obj).getEDate().equals(eDate))
			return true;
		else
			return false;
	}

	
			
}