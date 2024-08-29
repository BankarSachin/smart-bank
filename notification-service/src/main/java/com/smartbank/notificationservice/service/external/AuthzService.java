package com.smartbank.notificationservice.service.external;

import com.smartbank.notificationservice.exception.NotificationException;

public interface AuthzService {
	/**
	 * Checks Customer and Account relation existance
	 * @param tokenSubject
	 * @param accountNumber
	 * @return
	 * @throws AccsException
	 */
	boolean validateAccess(String tokenSubject,String accountNumber) throws NotificationException;
}
