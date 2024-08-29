package com.smartbank.transactionservice.exception;

import lombok.Getter;

/**
 * Base Custom Exception Class
 * @author Sachin
 */
@Getter
public class TxnException extends Exception{

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 5408624153197689855L;

	private final ExceptionCode exceptionCode;
	private final String[] codeArgs;
	private final ErrorStackTrace errorStackTrace;
	
	public TxnException() {
		super(ExceptionCode.TXNS_UNKNOWN_EXCEPTION.toString());
		this.exceptionCode = ExceptionCode.TXNS_UNKNOWN_EXCEPTION;
		this.codeArgs = new String[0];
		this.errorStackTrace = new ErrorStackTrace(this);
	}
	
	public TxnException(ExceptionCode exceptionCode,String...codeArgs) {
		super(exceptionCode.toString(codeArgs));
		this.exceptionCode = exceptionCode;
		this.codeArgs = codeArgs;
		this.errorStackTrace = new ErrorStackTrace(this);
	}
	
	public TxnException(ExceptionCode exceptionCode) {
		super(exceptionCode.toString());
		this.exceptionCode = exceptionCode;
		this.codeArgs = new String[0];
		this.errorStackTrace = new ErrorStackTrace(this);
	}
	
	public TxnException(ExceptionCode exceptionCode,String[] codeArgs,Throwable th) {
		super(exceptionCode.toString(codeArgs),th);
		this.exceptionCode = exceptionCode;
		this.codeArgs = codeArgs;
		this.errorStackTrace = new ErrorStackTrace(this);
	}
	
	public TxnException(ExceptionCode exceptionCode,Throwable th) {
		super(exceptionCode.toString(),th);
		this.exceptionCode = exceptionCode;
		this.codeArgs = new String[0];
		this.errorStackTrace = new ErrorStackTrace(this);
	}
}
