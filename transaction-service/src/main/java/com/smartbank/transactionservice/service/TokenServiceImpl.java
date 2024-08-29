package com.smartbank.transactionservice.service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.smartbank.transactionservice.dto.TokenResponse;
import com.smartbank.transactionservice.enums.ApiMessages;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService{

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

	@Override
	public Claims validateToken(String token) throws TxnException {
		try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TxnException(ExceptionCode.TXNS_JWT_ERROR,new String[] {ApiMessages.JWT_TOKEN_EXPIRED_ERROR.getMessage()});
        } catch (UnsupportedJwtException e) {
        	 throw new TxnException(ExceptionCode.TXNS_JWT_ERROR,new String[] {ApiMessages.JWT_TOKEN_UNSUPPORTED_ERROR.getMessage()});
        } catch (MalformedJwtException e) {
        	 throw new TxnException(ExceptionCode.TXNS_JWT_ERROR,new String[] {ApiMessages.JWT_TOKEN_MALFORMED_ERROR.getMessage()});
        } catch (SignatureException e) {
        	 throw new TxnException(ExceptionCode.TXNS_JWT_ERROR,new String[] {ApiMessages.JWT_TOKEN_INVALID_ERROR.getMessage()});
        } catch (IllegalArgumentException e) {
        	 throw new TxnException(ExceptionCode.TXNS_JWT_ERROR,new String[] {ApiMessages.JWT_TOKEN_EMPTY_ERROR.getMessage()});
        }
	}

	@Override
	public TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> authorities)
			throws TxnException {
		final String methodName = "generateToken";
		try {
	         final String token = Jwts.builder()
	        		 	.setHeaderParam("typ", "JWT")
	            		.setIssuer("SMBK")
	            		.setSubject(name)
	                    .claim("permissions", authorities.stream().map(GrantedAuthority :: getAuthority).collect(Collectors.joining(",")))
	                    .setIssuedAt(new Date())
	                    .setExpiration(new Date((new Date()).getTime() + expiration))
	                    .signWith(SignatureAlgorithm.HS512, secret).compact();
	          return new TokenResponse(token);
		} catch (Exception e) {
			log.error("{} - Unknown error while generting token {}",methodName,e.getMessage());
			throw new TxnException(ExceptionCode.TXNS_UNKNOWN_EXCEPTION, e);
		}
	}
}
