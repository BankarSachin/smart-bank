package com.smartbank.notificationservice.utils;

/**
 * @author Sachin
 */
public class DataMaskingUtil {
	
	private DataMaskingUtil() {
		//private constructor
	}
	
	public static String maskAccountNumber(String accountNumber) {
		if (accountNumber == null || accountNumber.length() < 4) {
			return accountNumber;
		}
		int unmaskedLength = 4;
		String maskedPart = accountNumber.substring(0, accountNumber.length() - unmaskedLength).replaceAll(".", "X");
		String unmaskedPart = accountNumber.substring(accountNumber.length() - unmaskedLength);
		return maskedPart + unmaskedPart;
	}
}
