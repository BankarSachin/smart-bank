package com.smartbank.transactionservice.service;

import com.smartbank.transactionservice.exception.TxnException;

public interface AuthzService {
	/**
	 * Checks Customer and Account relation existance
	 * @param tokenSubject
	 * @param accountNumber
	 * @return
	 * @throws AccsException
	 */
	boolean validateAccess(String tokenSubject,String accountNumber) throws TxnException;
}
