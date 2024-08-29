package com.smartbank.accountservice.dto;
import java.math.BigDecimal;
import java.util.UUID;

import com.smartbank.accountservice.enums.TransactionStatus;

/**
 * @author Sachin
 */
public record DepositResponse(TransactionStatus transactionStatus,UUID utrNumber,String accountNumber,BigDecimal amountDeposited,BigDecimal newBalance,String description) {

}
