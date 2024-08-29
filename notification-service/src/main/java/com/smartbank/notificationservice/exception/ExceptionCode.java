package com.smartbank.notificationservice.exception;

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
		return exceptionCodes.getOrDefault(key, ExceptionCode.NTFS_UNKNOWN_EXCEPTION);
	}
	
	//Server Error Series
	public static final ExceptionCode NTFS_UNKNOWN_EXCEPTION = new ExceptionCode("NTFS5001", "NTFS_UNKNOWN_EXCEPTION", "An unexcepted exception occured",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode NTFS_CUSTOMER_NON_EXIST = new ExceptionCode("NTFS5002", "NTFS_CUSTOMER_NON_EXIST", "Customer does not exists",HttpStatus.INTERNAL_SERVER_ERROR);
	public static final ExceptionCode NTFS_DB_EXCEPTION = new ExceptionCode("NTFS5002", "NTFS_DB_EXCEPTION", "Database level exception ocurred",HttpStatus.INTERNAL_SERVER_ERROR);
	
	
	
	//Client Input Error Series 
	public static final ExceptionCode NTFS_INVALID_INPUT = new ExceptionCode("NTFS4001", "NTFS_INVALID_INPUT", "Missing or invalid request parameters",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode NTFS_CUSTOMER_ALREADY_EXISTS = new ExceptionCode("NTFS4003", "NTFS_CUSTOMER_ALREADY_EXISTS", "Customer already exists",HttpStatus.BAD_REQUEST);
	public static final ExceptionCode NTFS_BAD_CREDENTIALS = new ExceptionCode("NTFS4004", "NTFS_BAD_CREDENTIALS", "Bad Credentials",HttpStatus.UNAUTHORIZED);
	public static final ExceptionCode NTFS_JWT_ERROR = new ExceptionCode("NTFS4003", "NTFS_JWT_ERROR", "Authentication failed.{0}",HttpStatus.UNAUTHORIZED);
	public static final ExceptionCode NTFS_AUTHZ_ERROR = new ExceptionCode("NTFS4005", "NTFS_AUTHZ_ERROR", "Autheorization error",HttpStatus.FORBIDDEN);

}
