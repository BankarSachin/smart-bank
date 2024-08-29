package com.smartbank.accountservice.response;

import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountType;

import lombok.Data;

/**
 * Response to be send user after registration of customer
 * @author Sachin
 */
@Data
public class RegistrationResponse {
	
	private Long customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String accountNumber;
    private String branch;
    private AccountType accountType;
    
    public RegistrationResponse(Customer customer) {
    	this.customerId = customer.getCustomerId();
    	this.name = customer.getName();
    	this.email = customer.getEmail();
    	this.phoneNumber = customer.getPhoneNumber();
    	this.accountNumber = customer.getAccount().get(0).getAccountNumber();
    	this.branch = customer.getAccount().get(0).getBranchCode();
    	this.accountType = customer.getAccount().get(0).getAccountType();
    }
}
