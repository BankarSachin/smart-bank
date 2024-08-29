package com.smartbank.accountservice.exception.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.smartbank.accountservice.constant.SysConstant;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ErrorStackTrace;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.exception.bean.ErrorInfo;
import com.smartbank.accountservice.exception.bean.ErrorStack;
import com.smartbank.accountservice.factory.ResponseEntityFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Central place to handle all exception in application.
 * <p>
 * Note : Extended ResponseEntityExceptionHandler for getting access to Invalid Request Exception caused by hibernate validation fails
 * @author Sachin
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

	/**
	 *Handle all Validations Errors
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		ExceptionCode exceptionCode = ExceptionCode.ACCS_INVALID_INPUT;
		
		for(FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ":"+ error.getDefaultMessage());
		}
		
		for(ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ":"+ error.getDefaultMessage());
		}
		
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(),errors,requestCorelationId,null);
		log.error(errorInfo.toString());
		return handleExceptionInternal(ex, errorInfo, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	/**
	 *Handle all Validations Errors
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		ExceptionCode exceptionCode = ExceptionCode.ACCS_INVALID_INPUT;
		errors.add(ex.getMessage());
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(),errors,requestCorelationId,null);
		log.error(errorInfo.toString());
		return handleExceptionInternal(ex, errorInfo, headers, HttpStatus.BAD_REQUEST, request);
	}
		
	/**
	 * Catch {@link AccsException} and Respond back with standardized error message
	 * @param ex {@link AccsException}
	 * @param request Web Request
	 * @return Error Response Entity
	 */
	@ExceptionHandler({AccsException.class})
	public ResponseEntity<Object> handleAccsException(AccsException ex, WebRequest request) {
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		List<String> causes = new ArrayList<>();
		List<ErrorStack> errorStacks = new ArrayList<>();
		
		ExceptionCode exceptionCode = ex.getExceptionCode();
		
		causes.add(exceptionCode.toString(ex.getCodeArgs()));
		errorStacks.add(ex.getErrorStackTrace().getErrorStack());
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(), causes, requestCorelationId, errorStacks);
		log.error(errorInfo.toString());
		return ResponseEntityFactory.getErrorResponse(request, errorInfo, exceptionCode.getHttpStatus());
	}
	
	/**
	 * Catch {@link BadCredentialsException} and Respond back with standardized error message
	 * @param ex {@link BadCredentialsException}
	 * @param request Web Request
	 * @return Error Response Entity
	 */
	@ExceptionHandler({BadCredentialsException.class})
	public ResponseEntity<Object> handleAccsException(BadCredentialsException ex, WebRequest request) {
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		List<String> causes = new ArrayList<>();
		List<ErrorStack> errorStacks = new ArrayList<>();
		
		ExceptionCode exceptionCode = ExceptionCode.ACCS_BAD_CREDENTIALS;
		
		ErrorStackTrace stackTrace = new ErrorStackTrace(ex);
		causes.add(exceptionCode.getMessage());
		errorStacks.add(stackTrace.getErrorStack());
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(), causes, requestCorelationId, errorStacks);
		log.error(errorInfo.toString());
		return ResponseEntityFactory.getErrorResponse(request, errorInfo, exceptionCode.getHttpStatus());
	}
	
	/**
	 * Catch {@link Exception} , {@link RuntimeException}and Respond back with standardized error message
	 * @param ex {@link Throwable}
	 * @param request Web Request
	 * @return Error Response Entity
	 */
	@ExceptionHandler({Exception.class,RuntimeException.class})
	public ResponseEntity<Object> handleAll(Throwable ex, WebRequest request) {
		final String requestCorelationId = request.getHeader(SysConstant.SYS_REQ_CORR_ID_HEADER);
		List<String> causes = new ArrayList<>();
		List<ErrorStack> errorStacks = new ArrayList<>();
		
		ExceptionCode exceptionCode = ExceptionCode.ACCS_UNKNOWN_EXCEPTION;
		
		ErrorStackTrace stackTrace = new ErrorStackTrace(ex);
		causes.add(exceptionCode.getMessage());
		errorStacks.add(stackTrace.getErrorStack());
		
		ErrorInfo errorInfo = new ErrorInfo(exceptionCode.getId(), causes, requestCorelationId, errorStacks);
		log.error(errorInfo.toString());
		return ResponseEntityFactory.getErrorResponse(request, errorInfo, exceptionCode.getHttpStatus());
		
	}
	
}
