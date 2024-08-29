package com.smartbank.transactionservice.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.smartbank.transactionservice.exception.bean.ErrorStack;

import lombok.Getter;

/**
 * Class to get Trace of messages out of exception
 * @author Sachin
 */
@Getter
public class ErrorStackTrace implements Serializable {

	/**
	 * Serial version UID for checksum
	 */
	private static final long serialVersionUID = 1L;

	private ExceptionCode exceptionCode;
	private List<String> errors = new ArrayList<>();
	
	
	public ErrorStackTrace(Throwable th) {
		this.exceptionCode = ExceptionCode.TXNS_UNKNOWN_EXCEPTION;
		for(Throwable exception = th; exception!=null;exception=exception.getCause()) {
			this.errors.add(exception.getMessage());
		}
	}
	
	public ErrorStack getErrorStack() {
		return new ErrorStack(exceptionCode.getKey(), errors);
	}
}
