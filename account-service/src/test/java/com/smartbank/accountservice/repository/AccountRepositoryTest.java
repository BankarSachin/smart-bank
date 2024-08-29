package com.smartbank.accountservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.AccountType;

@DataJpaTest
class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;
	
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Test
	void testgetNextSequenceValue(){

		Customer customer = new Customer();
		customer.setName("Sachin Bankr");
		customer.setEmail("sb@gmail.com");
		customer.setPhoneNumber("88784212");
		customer.setPassword("BBGgdbdddndhhdh");
		customer = customerRepository.save(customer);
		
		
		Account account = new Account();
		account.setAccountNumber("4878551221");
		account.setAccountStatus(AccountStatus.ACTIVE);
		account.setAccountType(AccountType.SAVINGS);
		account.setBranchCode("ABC001487");
		account.setCurrentBalance(BigDecimal.ZERO);
		account.setCustomer(customer);
		
		account = accountRepository.save(account);
		Account found = accountRepository.findByAccountNumber("4878551221").get();
		
		assertEquals("ABC001487",found.getBranchCode());

	}
	
	
	@Test
	void testNextVal() {
		Long actual = accountRepository.getNextSequenceValue();
		assertTrue(actual >= 1, "Expected value to be greater than " + 1);
	}

}
