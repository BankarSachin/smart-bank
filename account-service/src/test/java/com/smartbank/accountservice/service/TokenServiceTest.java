package com.smartbank.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.exception.AccsException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private Claims claims;

    @Spy
    @InjectMocks
    private TokenServiceImpl tokenService;

   
    @Test
    void testValidateTokenSuccess() throws AccsException {
    	
    	doReturn(3600000L).when(tokenService).getExpiration();
    	doReturn("U21hcnRCYW5r").when(tokenService).getSecret();
    	
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenService.getExpiration()))
                .signWith(SignatureAlgorithm.HS512, tokenService.getSecret())
                .compact();

        Claims resultClaims = tokenService.validateToken(token);
        assertEquals("testUser", resultClaims.getSubject());
    }

    @Test
    void testValidateTokenExpired() throws AccsException {
    	
    	//doReturn(3600000L).when(tokenService).getExpiration();
    	doReturn("U21hcnRCYW5r").when(tokenService).getSecret();
    	
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 0))
                .signWith(SignatureAlgorithm.HS512, tokenService.getSecret())
                .compact();

        AccsException ex = assertThrows(AccsException.class,()->tokenService.validateToken(token));
        assertEquals("Authentication failed.Authorization token has expired",ex.getMessage());
    }
    
    @Test
    void testValidateTokenEmpty() throws AccsException {
    	
		/*
		 * doReturn(3600000L).when(tokenService).getExpiration();
		 * doReturn("U21hcnRCYW5r").when(tokenService).getSecret();
		 */
    	AccsException ex = assertThrows(AccsException.class,()->tokenService.validateToken(""));
        assertEquals("Authentication failed.Authorization token is empty",ex.getMessage());
    }
    
    @Test
    void testValidateTokenSignatureException() throws AccsException {
    	
    	doReturn(3600000L).when(tokenService).getExpiration();
    	doReturn("U21hcnRCYW5r").when(tokenService).getSecret();
    	
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenService.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, tokenService.getSecret())
                .compact();

        AccsException ex = assertThrows(AccsException.class,()->tokenService.validateToken(token.replaceAll("i", "J")));
        assertEquals("Authentication failed.Authorization token is malformed",ex.getMessage());
    }
    
    @Test
    void testGenerateToken() throws AccsException {
    	doReturn(3600000L).when(tokenService).getExpiration();
    	doReturn("U21hcnRCYW5r").when(tokenService).getSecret();
    	
    	TokenResponse tokenResponse = tokenService.generateToken("testUser", List.of(new SimpleGrantedAuthority("ADMIN")));
    	assertNotNull(tokenResponse);
    }
}
