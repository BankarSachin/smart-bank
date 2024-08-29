package com.smartbank.transactionservice.service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.smartbank.transactionservice.config.GloablFeignClientDecoderConfiguration;
import com.smartbank.transactionservice.dto.NotificationRequest;
import com.smartbank.transactionservice.dto.NotificationResponse;

@FeignClient(name = "notification-service", configuration = GloablFeignClientDecoderConfiguration.class)
public interface NotificationService {
	
	/**
	 * @param headers
	 * @param accountNumber
	 * @param request
	 * @return
	 */
	@PostMapping(value = "${notification.service.notify.path}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<NotificationResponse> notify( @RequestHeader HttpHeaders headers,@PathVariable("accountnumber") String accountNumber, @RequestBody NotificationRequest request);
}
