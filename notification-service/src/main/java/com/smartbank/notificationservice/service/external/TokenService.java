package com.smartbank.notificationservice.service.external;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.smartbank.notificationservice.dto.external.TokenResponse;
import com.smartbank.notificationservice.exception.NotificationException;

import io.jsonwebtoken.Claims;

public interface TokenService {

	/**
	 * Validate Toke
	 * @param token
	 * @return
	 * @throws AccsException
	 */
	Claims validateToken(String token) throws NotificationException;
	
	/**
	 * Generate Token for autheticated user
	 * @param userDetails
	 * @return
	 */
	TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> authorities) throws NotificationException;
	
	
}
