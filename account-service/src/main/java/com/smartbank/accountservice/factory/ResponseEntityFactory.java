package com.smartbank.accountservice.factory;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.smartbank.accountservice.constant.SysConstant;
import com.smartbank.accountservice.exception.bean.ErrorInfo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Builds Reponse Entity in Case of Exceptions
 * @author Sachin
 */
public class ResponseEntityFactory {

	private ResponseEntityFactory() {
		//private constructor
	}
	
	public static ResponseEntity<Object> getErrorResponse(WebRequest request,ErrorInfo errorInfo,HttpStatus httpStatus){
		return ResponseEntity
				.status(httpStatus)
				.header(SysConstant.SYS_REQ_CORR_ID_HEADER,sanitize(request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER)))
				.header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
				.body(errorInfo);
	}

	private static String sanitize(String requestCorelationId) {
		return StringEscapeUtils.escapeHtml4(requestCorelationId);
	}
	
	public static ResponseEntity<Object> getErrorResponse(HttpServletRequest request,ErrorInfo errorInfo,HttpStatus httpStatus){
		return ResponseEntity
				.status(httpStatus)
				.header(SysConstant.SYS_REQ_CORR_ID_HEADER,sanitize(request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER)))
				.header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
				.body(errorInfo);
	}
}
