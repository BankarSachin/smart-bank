package com.smartbank.transactionservice.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.smartbank.transactionservice.exception.handler.GlobalAuthenticationEntryPoint;
import com.smartbank.transactionservice.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ProjectSecurityConfig {

	 private static final String[] PUBLIC_URLS = {
	            "/actuator/**"
	    };
	 
	 private final GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint;
	 private final JwtAuthenticationFilter jwtAuthenticationFilter;
	 
	 /**
	 * <p><b>SessionManagement</b> : Do not use Session Managment
	 * <p><b>CORS</b> : Check configuration in {@link corsConfigurationSource} method
	 * <p><b>CSRF</b> : Do not use Session Managment
	 * <p><b>SessionManagement</b> : Do not use Session Managment
	 * <p><b>SessionManagement</b> : Do not use Session Managment
	 * <p><b>SessionManagement</b> : Do not use Session Managment
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(
					 request -> request
					 			.requestMatchers(PUBLIC_URLS).permitAll()
					 			.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					 			.requestMatchers("/v1/transactions/*/entry").hasAnyAuthority("ADMIN")
					 			.requestMatchers("/v1/transactions/*/history").hasAnyAuthority("ADMIN")
					 			.requestMatchers("/v1/transactions/*/transfer").hasAnyAuthority("ADMIN")
					)
			.exceptionHandling(exhandler -> exhandler.authenticationEntryPoint(globalAuthenticationEntryPoint))
			.httpBasic(Customizer.withDefaults())
			.formLogin(formlogin -> formlogin.disable());
	
	 http.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
		
     return http.build();
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * These configurations once fetched by browser or client valid till 3600 Seconds i.e. 1 Hour
	 * @return
	 */
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setExposedHeaders(Collections.singletonList("*"));
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
