package com.smartbank.accountservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartbank.accountservice.constant.SysConstant;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.response.RegistrationResponse;
import com.smartbank.accountservice.service.CustomerService;
import com.smartbank.accountservice.service.TokenService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/")
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping(value = "/customer/register",consumes = { MediaType.APPLICATION_JSON_VALUE },produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RegistrationResponse> register(@RequestHeader Map<String, String> headers,
		@Valid @RequestBody CustomerAccountDTO customerAccountDto) throws AccsException {
		final String methodName = "createAccount";
			log.info("{} - Request received for Customer registration", methodName);
			final RegistrationResponse registrationResponse = customerService.registerCustomer(customerAccountDto);
			log.info("{} - Customer registered successfully with zero balance Account for {}", methodName,registrationResponse.getEmail());
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.header(SysConstant.SYS_REQ_CORR_ID_HEADER, headers.get(SysConstant.SYS_REQ_CORR_ID_HEADER.toLowerCase()))
					.body(registrationResponse);
	}
	
    /**
     * If customer login successful then return JWT token
     * JWT token required for subsequent requests
     * @return
     * @throws AccsException
     */
    @PostMapping("/customer/authenticate")
    public ResponseEntity<TokenResponse> authenticate(@RequestHeader Map<String, String> headers)
            throws AccsException {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	final TokenResponse tokenResponse = tokenService.generateToken(authentication.getName(),authentication.getAuthorities());
		return ResponseEntity
				.status(HttpStatus.OK)
				.header(SysConstant.SYS_REQ_CORR_ID_HEADER, headers.get(SysConstant.SYS_REQ_CORR_ID_HEADER.toLowerCase()))
				.body(tokenResponse);
    }
}
