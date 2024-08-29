package com.smartbank.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
import com.smartbank.accountservice.mapper.CustomerMapper;
import com.smartbank.accountservice.repository.CustomerRepository;
import com.smartbank.accountservice.response.RegistrationResponse;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
	
	@Mock
	private CustomerRepository customerRepository;
	
	@Mock
	private CustomerMapper customerMapper;
	
	@Mock
	private AccountService accountService;
	
	@InjectMocks
	private CustomerServiceImpl customerService;
	
	private CustomerAccountDTO customerAccountDTO;
	private Customer customer;
	private Account account;
	private String accountNumber;
	
	@BeforeEach
	public void setup() {
		accountNumber = "0988567890";
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
		customer.setName(customerAccountDTO.getName());
		customer.setEmail(customerAccountDTO.getEmail());
		customer.setPhoneNumber(customerAccountDTO.getPhoneNumber());
		customer.setPassword(new BCryptPasswordEncoder().encode(customerAccountDTO.getPassword()));
	}
	
	@Test
	void testRegisterNewCustomer() throws Exception {
		
		//Arrange
		when(customerRepository.existsByEmail(customerAccountDTO.getEmail())).thenReturn(Boolean.FALSE);
		when(customerRepository.existsByPhoneNumber(customerAccountDTO.getPhoneNumber())).thenReturn(Boolean.FALSE);
		when(customerRepository.save(customer)).thenReturn(customer);
		when(customerMapper.toEntity(customerAccountDTO)).thenReturn(customer);
		when(accountService.createAccount(customer, customerAccountDTO)).thenReturn(account);
		
		//act
		RegistrationResponse registrationResponse = customerService.registerCustomer(customerAccountDTO);
	
		//assert
		assertEquals(customerAccountDTO.getPhoneNumber(), registrationResponse.getPhoneNumber());
		
		//verify
		verify(customerRepository,times(2)).save(customer);
		
	}
	
	@Test
	void testRegisterNewCustomerFailWithDuplicateEmail() throws Exception {
		
		//Arrange
		when(customerRepository.existsByEmail(customerAccountDTO.getEmail())).thenReturn(Boolean.TRUE);
		
		//act
		AccsException  ex = assertThrows(AccsException.class,()->customerService.registerCustomer(customerAccountDTO));
		
		assertEquals(ExceptionCode.ACCS_CUSTOMER_ALREADY_EXISTS, ex.getExceptionCode());
		
		//verify
		verify(customerRepository,never()).save(customer);
		
	}
	
	@Test
	void testRegisterNewCustomerFailWithDuplicateMobileNumber() throws Exception {
		
		//Arrange
		when(customerRepository.existsByEmail(customerAccountDTO.getEmail())).thenReturn(Boolean.FALSE);
		when(customerRepository.existsByPhoneNumber(customerAccountDTO.getPhoneNumber())).thenReturn(Boolean.TRUE);
		
		//act
		AccsException  ex = assertThrows(AccsException.class,()->customerService.registerCustomer(customerAccountDTO));
		
		assertEquals(ExceptionCode.ACCS_CUSTOMER_ALREADY_EXISTS, ex.getExceptionCode());
		
		//verify
		verify(customerRepository,never()).save(customer);
		
	}
}
