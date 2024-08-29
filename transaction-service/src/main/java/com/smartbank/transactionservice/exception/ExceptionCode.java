package com.smartbank.transactionservice.exception;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Creating predefined exceptioncodes which helps addressing backend exceptions in UI
 * @author Sachin
 */
@Getter
public class ExceptionCode implements Serializable{

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 6905584233478237530L;
	private static Map<String, ExceptionCode> exceptionCodes = new HashMap<>();
	
	
	private String id;  //ACCS001
	private String key; //ACCS_INVALID_INPUT
	private String message;
	private HttpStatus httpStatus;
	
	
	public ExceptionCode(String id, String key, String message,HttpStatus httpStatus) {
		super();
		this.id = id;
		this.key = key;
		this.message = message;
		this.httpStatus = httpStatus;
		exceptionCodes.put(key, this);
	}
	
	@Override
	public String toString() {
		return this.message;
	}
	

	/**
	 * Formats exception based on input
	 * Suppose we define message as "invalid input {1}" and send argument [email]
	 * After message format final exception message would be "invalid input email" 
	 * @param args array of values
	 * @return formatted mexception message
	 */
	public String toString(String[] args) {
		String formattedString = this.message;
		if ( args!=null && args.length!=0) {
			try {
				MessageFormat messageFormat = new MessageFormat(message);
				formattedString = messageFormat.format(args);
			} catch (Exception e) {
				//do nothing
			}
		}
		return formattedString;
	}
	
	public static ExceptionCode getExceptionCode(String key) {
		return exceptionCodes.getOrDefault(key, ExceptionCode.TXNS_UNKNOWN_EXCEPTION);
	}
	
	//Server Error Series
	public static final ExceptionCode TXNS_UNKNOWN_EXCEPTION = new ExceptionCode("TXNS5001", "TXN_UNKNOWN_EXCEPTION", "An unexcepted exception occured",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode TXNS_CUSTOMER_NON_EXIST = new ExceptionCode("TXNS5002", "TXN_CUSTOMER_NON_EXIST", "Customer does not exists",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode TXNS_DB_EXCEPTION = new ExceptionCode("TXNS5003", "TXN_DB_EXCEPTION", "Database level exception ocurred",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode TXNS_NTFS_SERVICE_EMPTY_RESPONSE = new ExceptionCode("TXNS5004", "TXNS_NTFS_SERVICE_EMPTY_RESPONSE", "Empty response received from notification service",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode TXNS_NTFS_SERVICE_EXCEPTION = new ExceptionCode("TXNS500", "TXNS_NTFS_SERVICE_EXCEPTION", "Error {0}:{1} Received from notification service. ", HttpStatus.INTERNAL_SERVER_ERROR);

	public static final ExceptionCode TXNS_RMTE_SERVICE_EMPTY_RESPONSE = new ExceptionCode("TXNS5004", "TXNS_RMTE_SERVICE_EMPTY_RESPONSE", "Empty response received from remote service",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode TXNS_RMTE_SERVICE_EXCEPTION = new ExceptionCode("TXNS500", "TXNS_RMTE_SERVICE_EXCEPTION", "Error {0}:{1} Received from remote service. ", HttpStatus.INTERNAL_SERVER_ERROR);

	//Client Input Error Series 
	public static final ExceptionCode TXNS_INVALID_INPUT = new ExceptionCode("TXNS4001", "TXN_INVALID_INPUT", "Missing or invalid request parameters",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode TXNS_CUSTOMER_ALREADY_EXISTS = new ExceptionCode("TXNS4003", "TXN_CUSTOMER_ALREADY_EXISTS", "Customer already exists",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode TXNS_BAD_CREDENTIALS = new ExceptionCode("TXNS4004", "TXN_BAD_CREDENTIALS", "Bad Credentials",HttpStatus.UNAUTHORIZED);
	public static final ExceptionCode TXNS_JWT_ERROR = new ExceptionCode("TXNS4003", "TXN_JWT_ERROR", "Authentication failed.{0}",HttpStatus.UNAUTHORIZED);
	public static final ExceptionCode TXNS_AUTHZ_ERROR = new ExceptionCode("TXNS4005", "TXN_AUTHZ_ERROR", "Autheorization error",HttpStatus.FORBIDDEN);

	public static final ExceptionCode TXNS_SRC_ACCOUNT_NON_EXISTS = new ExceptionCode("TXNS4006", "TXNS_SRC_ACCOUNT_NON_EXISTS", "Debir Account {0} does not exists",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode TXNS_SRC_ACCOUNT_NON_ACTIVE = new ExceptionCode("TXNS4007", "TXNS_SRC_ACCOUNT_NON_ACTIVE", "Debit Account {0} is not Active",HttpStatus.BAD_REQUEST);
	
	public static final ExceptionCode TXNS_DEST_ACCOUNT_NON_EXISTS = new ExceptionCode("TXNS4008", "TXNS_DEST_ACCOUNT_NON_EXISTS", "Credit Account {0} does not exists",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode TXNS_DEST_ACCOUNT_NON_ACTIVE = new ExceptionCode("TXNS4009", "TXNS_DEST_ACCOUNT_NON_ACTIVE", "Credit Account {0} is not Active",HttpStatus.BAD_REQUEST);

	public static final ExceptionCode ACCS_INSUFFICIENT_BALANCE_EXCEPTION = new ExceptionCode("TXNS4010", "ACCS_INSUFFICIENT_BALANCE_EXCEPTION", "Debit Account {0} has insuffitient balance",HttpStatus.BAD_REQUEST);

}
