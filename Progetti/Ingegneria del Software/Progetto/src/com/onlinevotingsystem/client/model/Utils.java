package com.onlinevotingsystem.client.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	/**
	 * Restituisce l' MD5 hash della stringa passata
	 * @param string		cosa si desidera mappare in hash
	 * @return		stringa dell MD5 hash
	 */
	public static String MD5(String string) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(string.getBytes(), 0, string.length());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new BigInteger(1, m.digest()).toString(16);
	}
	
}
