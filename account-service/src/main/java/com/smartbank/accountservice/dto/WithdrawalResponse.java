package com.smartbank.accountservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.smartbank.accountservice.enums.TransactionStatus;

public record WithdrawalResponse(TransactionStatus transactionStatus,UUID utrNumber,String accountNumber,BigDecimal amountWithdrawn,BigDecimal newBalance,String description) {

}
