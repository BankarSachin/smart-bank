package com.smartbank.accountservice.service;

import com.smartbank.accountservice.exception.AccsException;

public interface AuthzService {
	/**
	 * Checks Customer and Account relation existance
	 * @param tokenSubject
	 * @param accountNumber
	 * @return
	 * @throws AccsException
	 */
	boolean validateAccess(String tokenSubject,String accountNumber) throws AccsException;
}
