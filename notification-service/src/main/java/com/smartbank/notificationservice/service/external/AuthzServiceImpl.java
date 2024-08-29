package com.smartbank.notificationservice.service.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.notificationservice.entity.external.Customer;
import com.smartbank.notificationservice.exception.ExceptionCode;
import com.smartbank.notificationservice.exception.NotificationException;
import com.smartbank.notificationservice.repository.external.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to implement Authorization 
 * @author Sachin
 */
@Service
@Slf4j
public class AuthzServiceImpl implements AuthzService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public boolean validateAccess(String tokenSubject, String accountNumber) throws NotificationException {
		final String methodName = "validateAccess";
		Customer customer = customerRepository.findByCustomerId(Long.parseLong(tokenSubject)).orElseThrow(
		 () -> new NotificationException(ExceptionCode.NTFS_AUTHZ_ERROR)		
		);
		
		boolean result = customer.getAccount().stream().anyMatch(acc -> acc.getAccountNumber().equals(accountNumber));
		if (!result) {
			log.error("{} - Customer does not own account number {} ", methodName,accountNumber);
			throw new NotificationException(ExceptionCode.NTFS_AUTHZ_ERROR);
		}
		log.info("{} - Customer owns account number {} ", methodName,accountNumber);
		return result;
	}

}
