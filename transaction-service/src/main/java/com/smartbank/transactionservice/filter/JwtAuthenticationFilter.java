package com.smartbank.transactionservice.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.transactionservice.constant.SysConstant;
import com.smartbank.transactionservice.exception.ErrorStackTrace;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;
import com.smartbank.transactionservice.exception.bean.ErrorInfo;
import com.smartbank.transactionservice.exception.bean.ErrorStack;
import com.smartbank.transactionservice.service.AuthzService;
import com.smartbank.transactionservice.service.TokenService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Validate JWT Toke else return
// Get Customer id from JWT toke
// Get Account Number from request
// Validate customer had this accout number else return
// Get all permission for that account number 
// Put into authority
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final TokenService tokenService;
	
	private final AuthzService authzService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String methodName = "doFilterInternal";
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("{} - User is already authenticated",methodName);
            filterChain.doFilter(request, response);
            return;
        }

        String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (requestTokenHeader == null) {
            filterChain.doFilter(request, response);
            log.warn("{} - Authorization header missing",methodName);
            return;
        }

        if (!requestTokenHeader.startsWith("Bearer ")) {
           throw new BadCredentialsException("Invalid Authorization header");
        }

        try {
        	 String token = requestTokenHeader.substring(7);
             final Claims claims =  tokenService.validateToken(token);
             
             final String customerid =  claims.getSubject();
             final String permissions = (String) claims.get("permissions");
             
             Optional<String> accountNumberOptional = extractAccountNumberFromPathParams(request);
             String accountNumber = accountNumberOptional.orElseThrow(()->new TxnException(ExceptionCode.TXNS_INVALID_INPUT));
             accountNumber = sanitizeAccountNumber(accountNumber);
             
             final boolean authz = authzService.validateAccess(customerid, accountNumber);
             log.info("{} - Authorization check status ? {}",methodName,authz);
             if (authz) {
             	 Authentication auth = new UsernamePasswordAuthenticationToken(customerid, null,
                             AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
             	 SecurityContextHolder.getContext().setAuthentication(auth);
     		}
		} catch (TxnException e) {
			log.error("{} - Error occured during JWT token validation {}", e.getMessage());
			handleError(request, response, e); // Had to handled this way because 
			return;
		}
        
        //No issues continue with Filter chain
		filterChain.doFilter(request, response);
	}
	
	private Optional<String> extractAccountNumberFromPathParams(HttpServletRequest request) {
		String pathInfo = request.getServletPath();
	    if (pathInfo != null) {
	        String[] parts = pathInfo.split("/");
	        int indexOfName = List.of(parts).indexOf("transactions");
	        if (indexOfName != -1) {
	            return Optional.of(parts[indexOfName + 1]);
	        }
	    }
	    return Optional.empty();
	}
	
	/**
	 * Remove all non number charaters from account number
	 * @param accountNumber 
	 * @return
	 */
	private String sanitizeAccountNumber(String accountNumber) {
		return accountNumber.replaceAll("[^0-9]", "");
	}

	/**
	 * Decides if Filter is to be called or not Return true if no need to Filter request
	 * @return true if no need to execute filter.
	 */
	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
		return List.of("/v1/customer/authenticate","/v1/customer/register").contains(request.getServletPath());
    }

	/**
	 * Error has been handled this way because Spring was eating our exception message and giving generic error message 
	 * through Authentication Entry Point
	 * @param request
	 * @param response
	 * @param accsException
	 * @throws IOException
	 */
	private void handleError(HttpServletRequest request,HttpServletResponse response,TxnException txnException) throws IOException {
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		List<String> causes = new ArrayList<>();
		List<ErrorStack> errorStacks = new ArrayList<>();
		
		ExceptionCode exceptionCode = ExceptionCode.TXNS_BAD_CREDENTIALS;
		
		ErrorStackTrace stackTrace = new ErrorStackTrace(txnException);
		causes.add(exceptionCode.getMessage());
		causes.add(txnException.getMessage());
		errorStacks.add(stackTrace.getErrorStack());
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(), causes, requestCorelationId, errorStacks);
		log.error(errorInfo.toString());
		writeErrorReponse(request, response,errorInfo);
	}
	
	private void writeErrorReponse(HttpServletRequest request, HttpServletResponse response,
			ErrorInfo errorInfo) throws IOException {
		response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		response.setHeader(SysConstant.SYS_REQ_CORR_ID_HEADER,request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER));
		response.getWriter().print(toJson(errorInfo));
	}
	
	private String toJson(ErrorInfo errorInfo) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(errorInfo);
		} catch (JsonProcessingException e) {
			// Do nothing
			return errorInfo.getCauses().toString();
		}
	}
}
