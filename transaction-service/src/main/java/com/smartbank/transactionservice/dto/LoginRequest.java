package com.smartbank.transactionservice.dto;

import lombok.Data;

/**
 * @author Sachin
 */
@Data
public class LoginRequest {
	private String loginId;
	private String password;
}