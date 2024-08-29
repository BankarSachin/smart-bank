package com.smartbank.transactionservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.transactionservice.entity.external.Customer;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;
import com.smartbank.transactionservice.repository.CustomerRepository;

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
	public boolean validateAccess(String tokenSubject, String accountNumber) throws TxnException {
		final String methodName = "validateAccess";
		Customer customer = customerRepository.findByCustomerId(Long.parseLong(tokenSubject)).orElseThrow(
		 () -> new TxnException(ExceptionCode.TXNS_AUTHZ_ERROR)		
		);
		
		boolean result = customer.getAccount().stream().anyMatch(acc -> acc.getAccountNumber().equals(accountNumber));
		if (!result) {
			log.error("{} - Customer does not own account number {} ", methodName,accountNumber);
			throw new TxnException(ExceptionCode.TXNS_AUTHZ_ERROR);
		}
		log.info("{} - Customer owns account number {} ", methodName,accountNumber);
		return result;
	}

}
