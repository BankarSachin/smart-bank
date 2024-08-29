package com.smartbank.accountservice.service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.enums.ApiMessages;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Getter
public class TokenServiceImpl implements TokenService{

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

	@Override
	public Claims validateToken(String token) throws AccsException {
		try {
            return Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new AccsException(ExceptionCode.ACCS_JWT_ERROR,ApiMessages.JWT_TOKEN_EXPIRED_ERROR.getMessage());
        } catch (UnsupportedJwtException e) {
        	 throw new AccsException(ExceptionCode.ACCS_JWT_ERROR,ApiMessages.JWT_TOKEN_UNSUPPORTED_ERROR.getMessage());
        } catch (MalformedJwtException e) {
        	 throw new AccsException(ExceptionCode.ACCS_JWT_ERROR,ApiMessages.JWT_TOKEN_MALFORMED_ERROR.getMessage());
        } catch (SignatureException e) {
        	 throw new AccsException(ExceptionCode.ACCS_JWT_ERROR,ApiMessages.JWT_TOKEN_INVALID_ERROR.getMessage());
        } catch (IllegalArgumentException e) {
        	 throw new AccsException(ExceptionCode.ACCS_JWT_ERROR,ApiMessages.JWT_TOKEN_EMPTY_ERROR.getMessage());
        }
	}

	/**
	 *Ideally RSA based JWT token should be generated. But for simplicity I have used SHA 512
	 */
	@Override
	public TokenResponse generateToken(String name, Collection<? extends GrantedAuthority> authorities)
			throws AccsException {
		final String methodName = "generateToken";
		try {
	         final String token = Jwts.builder()
	        		 	.setHeaderParam("typ", "JWT")
	            		.setIssuer("SMBK")
	            		.setSubject(name)
	                    .claim("permissions", authorities.stream().map(GrantedAuthority :: getAuthority).collect(Collectors.joining(",")))
	                    .setIssuedAt(new Date())
	                    .setExpiration(new Date((new Date()).getTime() + getExpiration()))
	                    .signWith(SignatureAlgorithm.HS512, getSecret()).compact();
	          return new TokenResponse(token);
		} catch (Exception e) {
			log.error("{} - Unknown error while generting token {}",methodName,e.getMessage());
			throw new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, e);
		}
	}
}
