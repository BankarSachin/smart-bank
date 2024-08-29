package com.smartbank.transactionservice.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request for Fund Transfer
 * @author Sachin
 */
@Data
public class TransferRequest{

	@NotNull(message = "Destination Account is Mandatory for Funds Transfer Request")
	private String destinationAccountNumber;
	
	@NotNull(message = "Transaction amount required for transaction entry")
	private BigDecimal transactionAmount;

	@Length(max=200,message = "Maximum length of transaction summary is 200")
	private String transactionSummary;
}
