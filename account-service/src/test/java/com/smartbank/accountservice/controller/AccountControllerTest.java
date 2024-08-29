package com.smartbank.accountservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.google.gson.Gson;
import com.smartbank.accountservice.dto.AccountTransaction;
import com.smartbank.accountservice.dto.DepositResponse;
import com.smartbank.accountservice.enums.TransactionStatus;
import com.smartbank.accountservice.service.AccountService;
import com.smartbank.accountservice.service.AuthzServiceImpl;
import com.smartbank.accountservice.utils.TestTokenGenerator;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	@Mock
	private AccountService accountService;
	
	@Mock
	private AuthzServiceImpl authzService;
	
	@InjectMocks
	private AccountController accountController;
	
	@Autowired
	private TestTokenGenerator tokenGenerator;
	
	private DepositResponse depositResponse;
	
	@BeforeEach
	public void setup() {
		depositResponse = new DepositResponse(
                TransactionStatus.SUCCESS,  
                UUID.randomUUID(),          
                "1234567890",               
                BigDecimal.valueOf(1000),   
                BigDecimal.valueOf(5000),   
                "Deposit successful"        
        );
	}
	
	@Test
	void depositSuccess() throws Exception {
		// Create mock data for request
        Map<String, String> headers = new HashMap<>();
        headers.put("X-SMTB-Request-Correlation-Id", "test-correlation-id");

        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTransactionAmount(BigDecimal.valueOf(1000));

        // Mock the service response
        when(accountService.deposit(headers, "1234567890", accountTransaction))
                .thenReturn(depositResponse);

        when(authzService.validateAccess(any(), any())).thenReturn(Boolean.TRUE);
        
        // Perform the request and assert the response
        ResultActions result = mockMvc.perform(post("/accounts/1234567890/deposit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+tokenGenerator.generateToken("345666", List.of(new SimpleGrantedAuthority("ADMIN"))))
                        .header("X-SMTB-Request-Correlation-Id", "test-correlation-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(accountTransaction))
                        .servletPath("/accounts/1234567890/deposit"))
                .andExpect(status().isOk());
        
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);
               
	}
	
	private String toJson(final Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
}
