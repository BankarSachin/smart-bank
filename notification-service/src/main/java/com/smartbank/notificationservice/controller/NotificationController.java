package com.smartbank.notificationservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartbank.notificationservice.constant.SysConstant;
import com.smartbank.notificationservice.dto.NotificationRequest;
import com.smartbank.notificationservice.dto.NotificationResponse;
import com.smartbank.notificationservice.enums.NotificationType;
import com.smartbank.notificationservice.service.EmailService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends mail to given email address
 * 
 * @author Sachin
 */
@RestController
@RequestMapping("/v1/")
@Slf4j
public class NotificationController {
	@Autowired
	private EmailService emailService;

	@PostMapping(value = "/notifications/{accountnumber}/notify", 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotificationResponse> notify(@RequestHeader Map<String, String> headers,
			@PathVariable(name = "accountnumber") String accountNumber, @Valid @RequestBody NotificationRequest request)
			throws Exception {
		NotificationResponse notificationResponse = emailService.sendEmail(accountNumber, request);
		log.info("notify - Transaction alert response {}",notificationResponse);
		if(request.getNotificationType()== NotificationType.TRANSFER) {
			NotificationRequest transferCreditAlertRequest = new NotificationRequest();
			transferCreditAlertRequest.setNotificationType(NotificationType.CREDIT);
			transferCreditAlertRequest.setTxnAmmount(request.getTxnAmmount());
			transferCreditAlertRequest.setTxnDateTime(request.getTxnDateTime());
			transferCreditAlertRequest.setCurrentBalance(request.getDestinationCurrentBalance());
			transferCreditAlertRequest.setUtrNumber(request.getUtrNumber());
			NotificationResponse transferCreditAlertResponse = emailService.sendEmail(request.getDestinationAccountNumber(), transferCreditAlertRequest);
			log.info("notify - Transfer credit alert sent to payee {}",transferCreditAlertResponse);
		}
		return ResponseEntity
					.status(HttpStatus.OK)
					.header(SysConstant.SYS_REQ_CORR_ID_HEADER, headers.get(SysConstant.SYS_REQ_CORR_ID_HEADER.toLowerCase()))
					.body(notificationResponse);
	}
}
