package com.smartbank.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.AccountType;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class AuthzServiceTest {

	@Mock
	private CustomerRepository customerRepository;
	
	@InjectMocks
	private AuthzServiceImpl authzService;

	private CustomerAccountDTO customerAccountDTO;
	private Customer customer;
	private Account account;
	private String accountNumber;
	private Long customerId;
	
	@BeforeEach
	public void setup() {
		accountNumber = "0988567890";
		customerId = 12345678L;
		customerAccountDTO = new CustomerAccountDTO();
		customerAccountDTO.setAccountType(AccountType.SAVINGS);
		customerAccountDTO.setAmount(BigDecimal.ZERO);
		customerAccountDTO.setBranchCode("ABCD0000988");
		customerAccountDTO.setEmail("SachinBankar1512@gmail.com");
		customerAccountDTO.setPassword("SecurePassword123");
		customerAccountDTO.setPhoneNumber("9987823428");
		
		account = new Account();
		account.setAccountNumber(accountNumber);
		account.setAccountStatus(AccountStatus.ACTIVE);
		account.setCurrentBalance(BigDecimal.valueOf(5000));
	
		customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setName(customerAccountDTO.getName());
		customer.setEmail(customerAccountDTO.getEmail());
		customer.setPhoneNumber(customerAccountDTO.getPhoneNumber());
		customer.setPassword(new BCryptPasswordEncoder().encode(customerAccountDTO.getPassword()));

		customer.setAccount(List.of(account));
	}
	
	@Test
	void testValidateAccess() throws AccsException {
		//Arrabge
		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(customer));
	
		//Act
		Boolean bool = authzService.validateAccess(customerId.toString(), accountNumber);
		assertTrue(bool);
		
		verify(customerRepository,times(1)).findByCustomerId(customerId);
	}
	
	@Test
	void testValidateAccessAuthzErrorOnNoAccOwner() {
		//Arrabge
		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(customer));
	
		AccsException ex = assertThrows(AccsException.class, ()->authzService.validateAccess(customerId.toString(),"NOACC"));
		assertEquals(ExceptionCode.ACC_AUTHZ_ERROR, ex.getExceptionCode());
		
		verify(customerRepository,times(1)).findByCustomerId(customerId);
	}

	@Test
	void testValidateAccessAuthzErrorOnCustomerNonExistsnce() {
		//Arrabge
		when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
	
		AccsException ex = assertThrows(AccsException.class, ()->authzService.validateAccess(customerId.toString(),"NOACC"));
		assertEquals(ExceptionCode.ACC_AUTHZ_ERROR, ex.getExceptionCode());
		
		verify(customerRepository,times(1)).findByCustomerId(customerId);
	}
}
