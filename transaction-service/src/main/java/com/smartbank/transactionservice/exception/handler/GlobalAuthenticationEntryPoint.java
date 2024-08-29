package com.smartbank.transactionservice.exception.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.transactionservice.constant.SysConstant;
import com.smartbank.transactionservice.exception.ErrorStackTrace;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.bean.ErrorInfo;
import com.smartbank.transactionservice.exception.bean.ErrorStack;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		List<String> causes = new ArrayList<>();
		List<ErrorStack> errorStacks = new ArrayList<>();
		
		ExceptionCode exceptionCode = ExceptionCode.TXNS_BAD_CREDENTIALS;
		
		ErrorStackTrace stackTrace = new ErrorStackTrace(authException);
		causes.add(exceptionCode.getMessage());
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
			return "Bad Credentials";
		}
	}

}
