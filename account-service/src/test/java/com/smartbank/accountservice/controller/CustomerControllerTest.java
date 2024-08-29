package com.smartbank.accountservice.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.smartbank.accountservice.constant.SysConstant;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.AccountType;
import com.smartbank.accountservice.response.RegistrationResponse;
import com.smartbank.accountservice.service.CustomerService;
import com.smartbank.accountservice.service.TokenService;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
	@Autowired
    private MockMvc mockMvc;

	@Mock
	private CustomerService customerService;
	
	@Mock
	private TokenService tokenService;
	
	@InjectMocks
	private CustomerController customerController;
	
	private CustomerAccountDTO customerAccountDTO;
	private Map<String, String> headers;
	private RegistrationResponse registrationResponse;
	
	@BeforeEach
	public void setup() {
		headers = new HashMap<>();
		headers.put("X-SMTB-Request-Correlation-Id", UUID.randomUUID().toString());
		headers.put(HttpHeaders.AUTHORIZATION, "Bearer ");
		
		Account account = new Account();
		account.setAccountNumber("0988567890");
		account.setAccountStatus(AccountStatus.ACTIVE);
		account.setCurrentBalance(BigDecimal.valueOf(5000));
	
		
		customerAccountDTO = new CustomerAccountDTO();
		customerAccountDTO.setAccountType(AccountType.SAVINGS);
		customerAccountDTO.setAmount(BigDecimal.ZERO);
		customerAccountDTO.setBranchCode("ABCD0000988");
		customerAccountDTO.setEmail("SachinBankar1512@gmail.com");
		customerAccountDTO.setPassword("SecurePassword123");
		customerAccountDTO.setPhoneNumber("9987823428");
		customerAccountDTO.setName("Sachin");
		
		Customer customer = new Customer();
		customer.setCustomerId(1234567L);
		customer.setAccount(List.of(account));
		customer.setEmail(customerAccountDTO.getEmail());
		customer.setName(customerAccountDTO.getName());
		customer.setPhoneNumber(customerAccountDTO.getPhoneNumber());
		registrationResponse = new RegistrationResponse(customer);
	
	}
	
	@Test
	void testRegister() throws Exception {
		 when(customerService.registerCustomer(any(CustomerAccountDTO.class)))
         .thenReturn(registrationResponse);
		 
		mockMvc.perform(post("/v1/customer/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(customerAccountDTO))
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andExpect(jsonPath("$.name", is("Sachin")));
				
	}
	
	
	@Test
	void testTokenGeneration() throws Exception{
	    // Mocking SecurityContext and Authentication
	    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
	    when(authentication.getName()).thenReturn("user123");

	    // Mocking authorities as a List of SimpleGrantedAuthority
	    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
	    
	    // Using doReturn to avoid type mismatch issues
	    doReturn(authorities).when(authentication).getAuthorities();

	    SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
	    when(securityContext.getAuthentication()).thenReturn(authentication);
	    SecurityContextHolder.setContext(securityContext);
	    
	    TokenResponse tokenResponse = new TokenResponse("mockedToken");
	    when(tokenService.generateToken(anyString(), anyCollection())).thenReturn(tokenResponse);
	    
	 // Define the headers map
	    Map<String, String> headers = new HashMap<>();
	    headers.put(SysConstant.SYS_REQ_CORR_ID_HEADER.toLowerCase(), "test-correlation-id");

	    // Perform the request and assert the response
	    mockMvc.perform(post("/v1/customer/authenticate")
	                    .header(SysConstant.SYS_REQ_CORR_ID_HEADER, "test-correlation-id")
	                    .servletPath("/v1/customer/authenticate"))
	            .andExpect(status().isOk());
	}
	
	private String toJson(final Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
}
