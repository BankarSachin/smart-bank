package com.smartbank.accountservice.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.exception.AccsException;

import io.jsonwebtoken.Claims;

public interface TokenService {

	/**
	 * Validate Toke
	 * @param token
	 * @return
	 * @throws AccsException
	 */
	Claims validateToken(String token) throws AccsException;
	
	/**
	 * Generate Token for autheticated user
	 * @param userDetails
	 * @return
	 */
	TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> authorities) throws AccsException;
	
	
}
