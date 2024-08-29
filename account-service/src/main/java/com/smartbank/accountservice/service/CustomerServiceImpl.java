package com.smartbank.accountservice.service;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.mapper.CustomerMapper;
import com.smartbank.accountservice.repository.CustomerRepository;
import com.smartbank.accountservice.response.RegistrationResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles customer related operations like Register Customer,Update Customer Data
 * @author Sachin
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private AccountService accountService;
	
	@Override
	@Transactional
	public RegistrationResponse registerCustomer(CustomerAccountDTO customerDto) throws AccsException {
		final String methodName = "registerCustomer";
		try {
			if (doesCustomerExistsByEmail(customerDto.getEmail())) {
				throw new AccsException(ExceptionCode.ACCS_CUSTOMER_ALREADY_EXISTS);
			}
			
			if (doesCustomerExistsByPhoneNumber(customerDto.getPhoneNumber())) {
				throw new AccsException(ExceptionCode.ACCS_CUSTOMER_ALREADY_EXISTS);
			}
			final Customer registeredCustomer = customerRepository.save(customerMapper.toEntity(customerDto));
			log.info("{} - Customer registered successfully! {}", methodName,registeredCustomer.getEmail());
			Account account = accountService.createAccount(registeredCustomer,customerDto);
			log.info("{} - Created zero balance account as well", methodName);
			List<Account> accounts = new ArrayList<>();
			accounts.add(account);
			registeredCustomer.setAccount(accounts);
			customerRepository.save(registeredCustomer);
			log.info("{} - Setup Link between customer and account", methodName);
			return new RegistrationResponse(registeredCustomer);
		} catch (AccsException e) {
			log.error("{} - Error while registering customer {}",methodName,e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("{} - Unknown error while registering customer {}",methodName,e.getMessage());
			throw new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, e);
		}
	}
	
	@Override
	public boolean doesCustomerExistsByEmail(String email) {
		return customerRepository.existsByEmail(email);
	}

	@Override
	public boolean doesCustomerExistsByPhoneNumber(String phoneNumber) {
		return customerRepository.existsByPhoneNumber(phoneNumber);
	}
}