package com.smartbank.accountservice.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountTransaction {
	
	@NotNull(message = "Transaction amount required for transaction entry")
	private BigDecimal transactionAmount;
	
	@Length(max = 200,message = "Summary should be less than 200 characters")
	private String transactionSummary;
}
