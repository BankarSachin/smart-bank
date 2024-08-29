package com.smartbank.notificationservice.service;

import static com.smartbank.notificationservice.utils.Mappers.fnMappper;
import static com.smartbank.notificationservice.utils.Mappers.fnTransferMappper;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.smartbank.notificationservice.dto.NotificationRequest;
import com.smartbank.notificationservice.dto.NotificationResponse;
import com.smartbank.notificationservice.entity.external.Account;
import com.smartbank.notificationservice.entity.external.Customer;
import com.smartbank.notificationservice.enums.ApiMessages;
import com.smartbank.notificationservice.enums.NotificationType;
import com.smartbank.notificationservice.repository.external.AccountRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
	private String from;
	
	@Autowired
    private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;
	
	
	@Autowired
	private AccountRepository accountRepository;
   
	/**
	 * How to Get Email and another detils ?
	 * 	1> Option Call service endpoint
	 *  2> Call Customer repository and Fetch from DB  [ If Time permits this approach would be converted to REST call to customer service]
	 * @throws Exception 
	 *
	 */
	@Override
	@Retryable(
			retryFor = { RuntimeException.class }, 
	        maxAttempts = 3, 
	        backoff = @Backoff(delay = 2000)
	)
	public NotificationResponse sendEmail(String accountNumber, NotificationRequest notificationRequest) throws Exception {
		final String methodName = "sendEmail";
		try {
			  String templateName;
			  String responseText;
			  Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
			  Customer customer = account.getCustomer();
			  
			  MimeMessage message = mailSender.createMimeMessage();
			  MimeMessageHelper helper = new MimeMessageHelper(message, true);
			  
			  helper.setTo(customer.getEmail());
			  helper.setFrom(from);
			  final NotificationType notificationType = notificationRequest.getNotificationType();
			  
			  Map<String, Object> contextvars = switch (notificationType) {
					case CREDIT: {
						helper.setSubject(ApiMessages.MAIL_SUB_CREDIT.getMessage());
						responseText = ApiMessages.MAIL_SUB_CREDIT_RESPONSE_TEXT.getMessage();
						templateName = "cash-deposit";
						yield fnMappper.apply(accountNumber,notificationRequest);
					}
					case DEBIT: {
						helper.setSubject(ApiMessages.MAIL_SUB_DEBIT.getMessage());
						responseText = ApiMessages.MAIL_SUB_DEBIT_RESPONSE_TEXT.getMessage();
						templateName = "cash-withdrawal";
						yield fnMappper.apply(accountNumber,notificationRequest);
					}
					case TRANSFER: {
						helper.setSubject(ApiMessages.MAIL_SUB_TRANSFER.getMessage());
						responseText = ApiMessages.MAIL_SUB_TRANSFER_RESPONSE_TEXT.getMessage();
						templateName = "transfer-success";
						yield fnTransferMappper.apply(accountNumber,notificationRequest);
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + notificationType);
			  };
			  
			  Context context = new Context(Locale.ENGLISH, contextvars);
			  String htmlContent = templateEngine.process(templateName, context);
			  
			  // Set the email content
		      helper.setText(htmlContent, true); // true indicates HTML content
		      
		      // Send the email
		      mailSender.send(message);
		      log.info("{} - mail sent successfully", methodName);
		      return new NotificationResponse("Success",responseText);
			
		} catch (Exception e) {
			log.info("{} - Error in mail sent {}", methodName,e.getMessage());
			throw e;
		}
	}
	
	@Recover
	public NotificationResponse sendEmail(RuntimeException ex) throws Exception {
		return new NotificationResponse("Failed", "Tranaction Mail not send due to "+ex.getMessage());
	}

}
