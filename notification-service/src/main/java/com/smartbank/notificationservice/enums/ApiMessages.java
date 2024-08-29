package com.smartbank.notificationservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiMessages {
	MAIL_SUB_CREDIT("Account Credit Alert"),
	MAIL_SUB_CREDIT_RESPONSE_TEXT("Account Credit Alert Mail Sent Successfully"),
	MAIL_SUB_DEBIT("Account Debit Alert"),
	MAIL_SUB_DEBIT_RESPONSE_TEXT("Account Debit Alert Mail Sent Successfully"),
	MAIL_SUB_TRANSFER("Fund Transfer - To Another Account"),
	MAIL_SUB_TRANSFER_RESPONSE_TEXT("Fund Transfer - To Another Account Alert Mail Sent Successfully"),
    JWT_TOKEN_EMPTY_ERROR("Authorization token is empty"),
    JWT_TOKEN_EXPIRED_ERROR("Authorization token has expired"),
    JWT_TOKEN_INVALID_ERROR("Authorization token is invalid"),
    JWT_TOKEN_MALFORMED_ERROR("Authorization token is malformed"),
    JWT_TOKEN_NOT_FOUND_ERROR("Authorization token not found"),
    JWT_TOKEN_SIGNATURE_INVALID_ERROR("Authorization token signature is invalid"),
    JWT_TOKEN_UNSUPPORTED_ERROR("Authorization token is not supported");
	
	private final String message;
}
