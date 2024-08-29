package com.smartbank.accountservice.service.external;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.google.gson.Gson;
import com.smartbank.accountservice.constant.SysConstant;
import com.smartbank.accountservice.dto.NotificationRequest;
import com.smartbank.accountservice.dto.NotificationResponse;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.exception.bean.ErrorInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sachin
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceClient{

	private final NotificationService notificationService;

	/**
	 * To be Qualified for Asynch
	 *  1> Should be public
	 *  2> Should Return COmpletableFuture  (Future is Blocking and CompletableFuture not)
	 *  3> Should not throw checked exception
	 */
	@Async
	public CompletableFuture<NotificationResponse> notifyTransfer(Map<String, String> headers, String debitAccountNumber,
			NotificationRequest notificationRequest){
		final String methodName = "notifyTransfer";
		CompletableFuture<NotificationResponse> notificationFuture = new CompletableFuture<>();
		try {
			HttpHeaders txnHeaders = getHeaders(headers);
			txnHeaders.add(HttpHeaders.USER_AGENT, "account-service");
			
			ResponseEntity<NotificationResponse> notificationResponse = notificationService.notify(txnHeaders, debitAccountNumber, notificationRequest);
			
			if (notificationResponse.hasBody()) {
				log.info("{} - Notification sent successfully {}", methodName, notificationResponse.getBody());
				notificationFuture.complete(notificationResponse.getBody());
			}else {
				log.error("{} - Empty response received from notification service", methodName);
				notificationFuture.completeExceptionally(new AccsException(ExceptionCode.ACCS_RMTE_SERVICE_EMPTY_RESPONSE));
			}
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			ErrorInfo errorInfo = new Gson().fromJson(ex.getResponseBodyAsString(), ErrorInfo.class);
			log.info("{} - Error received from transaction service {} : {}", methodName, errorInfo.getCode(),errorInfo.getCauses());
			notificationFuture.completeExceptionally(new AccsException(ExceptionCode.ACCS_RMTE_SERVICE_EXCEPTION,errorInfo.getCode(), errorInfo.getCauses().get(0)));
		} catch (ResourceAccessException ex) {
			log.error("{} - Connection error occurred while calling Transaction Service {}", methodName,ex.getMessage());
			notificationFuture.completeExceptionally(new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, ex));
		} catch (RestClientException ex) {
			log.error("{} - Client error occurred while calling Transaction Service {}", methodName, ex.getMessage());
			notificationFuture.completeExceptionally(new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, ex));
		} catch (Exception ex) {
			log.error("{} - Unknown error occurred while calling Transaction Service {}", methodName, ex.getMessage());
			notificationFuture.completeExceptionally(new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, ex));
		}
		return notificationFuture;
	}

	/** Why Lower case ? tomcat converts header to lowercase
	 * @param headers
	 * @return
	 */
	private HttpHeaders getHeaders(Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.add(SysConstant.SYS_REQ_CORR_ID_HEADER, headers.getOrDefault(SysConstant.SYS_REQ_CORR_ID_HEADER.toLowerCase(), "4554"));
		httpHeaders.add(HttpHeaders.AUTHORIZATION, headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()));
		return httpHeaders;
	}
}
