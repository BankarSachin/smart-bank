package com.smartbank.accountservice.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.entity.Customer;


/**
 * Maps {@link CustomerAccountDTO} to new {@link Customer}
 * @author Sachin
 */
@Component
public class CustomerMapper {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public Customer toEntity(CustomerAccountDTO customerDto) {
		Customer customer = new Customer();
		customer.setName(customerDto.getName());
		customer.setEmail(customerDto.getEmail());
		customer.setPhoneNumber(customerDto.getPhoneNumber());
		customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
		return customer;
	}
}
