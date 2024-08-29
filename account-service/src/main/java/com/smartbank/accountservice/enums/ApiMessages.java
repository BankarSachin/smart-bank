package com.smartbank.accountservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiMessages {
    JWT_TOKEN_EMPTY_ERROR("Authorization token is empty"),
    JWT_TOKEN_EXPIRED_ERROR("Authorization token has expired"),
    JWT_TOKEN_INVALID_ERROR("Authorization token is invalid"),
    JWT_TOKEN_MALFORMED_ERROR("Authorization token is malformed"),
    JWT_TOKEN_NOT_FOUND_ERROR("Authorization token not found"),
    JWT_TOKEN_SIGNATURE_INVALID_ERROR("Authorization token signature is invalid"),
    JWT_TOKEN_UNSUPPORTED_ERROR("Authorization token is not supported");

    private final String message;

}
