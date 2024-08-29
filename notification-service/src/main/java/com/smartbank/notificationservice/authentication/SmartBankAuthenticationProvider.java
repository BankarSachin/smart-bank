package com.smartbank.notificationservice.authentication;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.smartbank.notificationservice.entity.external.Customer;
import com.smartbank.notificationservice.entity.external.Permissions;
import com.smartbank.notificationservice.repository.external.CustomerRepository;

/**
 * This custom authentication provider would get internally called by {@link ProviderManager}
 * <p>
 * What it does ?
 * <p>
 * It fetch Customer details by email id or user id using {@link CustomerRepository}
 * Asks password encoder i.e. {@link BCryptPasswordEncoder} to check password if it matches then creates Authentication object with all permissions 
 * else throw {@link BadCredentialsException}
 * @author Sachin
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SmartBankAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String loginId = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        
        Customer customer = null;
        if (isNumeric(loginId)) {
        	customer = loadCustomerByCustomerId(Long.parseLong(loginId));
        } else if (isValidEmail(loginId)) {
        	customer = loadCustomerByCustomerEmail(loginId);
        } else {
            throw new BadCredentialsException("Invalid customerid or email format");
        }
        if (passwordEncoder.matches(pwd, customer.getPassword())) {
            return new UsernamePasswordAuthenticationToken(customer.getCustomerId(), pwd, getGrantedAuthorities(customer.getCustomerPermissions()));
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
	}
	
	@SuppressWarnings("unused")
	private Collection<? extends GrantedAuthority> getDefaultGrantedAuthorities() {
		return List.of(new SimpleGrantedAuthority("ADMIN"),new SimpleGrantedAuthority("DEPOSIT_FUNDS"));
	}
	
	private Collection<? extends GrantedAuthority> getGrantedAuthorities(List<Permissions> permissions) {
		return permissions.stream()
				.map(t -> new SimpleGrantedAuthority(t.getPermission()))
				.toList();
	}

	private Customer loadCustomerByCustomerEmail(String emailid) {
		return customerRepository.findByEmail(emailid).orElseThrow(()-> new BadCredentialsException("Customer not found with email "+emailid));
	}

	private Customer loadCustomerByCustomerId(long customerId) {
		return customerRepository.findByCustomerId(customerId).orElseThrow(()-> new BadCredentialsException("Customer not found with id "+customerId));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	private boolean isNumeric(String str) {
		return str != null && str.matches("\\d+");
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		Pattern pattern = Pattern.compile(emailRegex);
		return pattern.matcher(email).matches();
	}

}
