package com.smartbank.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartbank.transactionservice.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {
	
	@NotNull(message = "Transaction amount required for transaction entry")
	private BigDecimal transactionAmount;
	
	@NotNull(message = "Closing balance required for transaction entry")
	private BigDecimal closingBalance;
	
	private String transactionSummary;
	
	@NotNull(message = "Transaction type is required")
	private TransactionType transactionType;
	
	@NotNull(message = "Transacction Timestamp Needed")
	private LocalDateTime transactionDate;
}
