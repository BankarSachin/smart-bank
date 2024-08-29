package com.smartbank.accountservice.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.smartbank.accountservice.dto.TokenResponse;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;

@Component
@Data
public class TestTokenGenerator {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	public String generateToken(String name, Collection<? extends GrantedAuthority> authorities)
			throws AccsException {
		try {
			final String token = Jwts.builder().setHeaderParam("typ", "JWT").setIssuer("SMBK").setSubject(name)
					.claim("permissions",
							authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
					.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + getExpiration()))
					.signWith(SignatureAlgorithm.HS512, getSecret()).compact();
			return token;
		} catch (Exception e) {
			throw new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, e);
		}
	}
}
