package com.smartbank.transactionservice.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.smartbank.transactionservice.dto.TokenResponse;
import com.smartbank.transactionservice.exception.TxnException;

import io.jsonwebtoken.Claims;

public interface TokenService {

	/**
	 * Validate Toke
	 * @param token
	 * @return
	 * @throws AccsException
	 */
	Claims validateToken(String token) throws TxnException;
	
	/**
	 * Generate Token for autheticated user
	 * @param userDetails
	 * @return
	 */
	TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> authorities) throws TxnException;
	
	
}
