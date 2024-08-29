package com.smartbank.accountservice.service;

import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.response.RegistrationResponse;

/**
 * Handles Customer Related operations
 *  1> Create User
 * @author Sachin
 */
public interface CustomerService {

	/**
	 * This performs two tasks
	 * Create Customer
	 * Create zero balance account
	 * @param customerDto Input from front end to create/register customer
	 * @return Generated Customer details
	 */
	RegistrationResponse registerCustomer(CustomerAccountDTO customerDto) throws AccsException;
	
	/**
	 * CHecks is customer already exists or not by email. 
	 * No two customers should have same email id
	 * @param email
	 * @return
	 */
	boolean doesCustomerExistsByEmail(String email);
	
	/**
	 * Checks if customer with same mobile exists. 
	 * No two customers are allowed to have same mobile number
	 * In Real world scenario mobile numbers keeps on changing. For that purpose another 
	 * endpoint for update customer info can be developed
	 * In that endpoint we can have OTP based verifiction.OTP would be sent to new mobile number.
	 * But for simplicity we have kept unique mobile number 
	 * @param phoneNumber
	 * @return
	 */
	boolean doesCustomerExistsByPhoneNumber(String phoneNumber);
}
