package com.smartbank.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.smartbank.transactionservice.enums.TransactionStatus;
import com.smartbank.transactionservice.enums.TransactionType;

import lombok.Data;

/**
 * Reponse for transaction
 * @author Sachin
 */
@Data
public class TransactionResponse{
	private UUID utrNumber;
	private TransactionStatus transactionStatus;
	private TransactionType transactionType;
	private BigDecimal transactionAmount;
	private LocalDateTime transactionDate;
	private String transactionAccount;
	private BigDecimal closingBalance;
	private String transactionDescription;
}
