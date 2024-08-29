package com.smartbank.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.smartbank.accountservice.enums.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
	
	@NotNull
	private NotificationType notificationType;
	
	private String destinationAccountNumber; 
	
	private BigDecimal destinationCurrentBalance;
	
	
	@NotNull
	private BigDecimal txnAmmount;
	
	@NotNull
	private LocalDateTime txnDateTime;
	
	@NotNull
	private BigDecimal currentBalance;
	
	@NotNull
	private UUID utrNumber;
}
