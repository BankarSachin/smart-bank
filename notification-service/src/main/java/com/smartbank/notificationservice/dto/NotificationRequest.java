package com.smartbank.notificationservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartbank.notificationservice.customvalidator.ValidDestinationAccount;
import com.smartbank.notificationservice.enums.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidDestinationAccount(message = "Destination account number is required for transfer notifications.")
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
	private String utrNumber;
}
