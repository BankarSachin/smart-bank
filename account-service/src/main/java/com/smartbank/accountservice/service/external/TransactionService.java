package com.smartbank.accountservice.service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.smartbank.accountservice.config.GloablFeignClientDecoderConfiguration;
import com.smartbank.accountservice.dto.TransactionRequest;
import com.smartbank.accountservice.dto.TransactionResponse;

@FeignClient(name = "transaction-service", configuration = GloablFeignClientDecoderConfiguration.class)
public interface TransactionService {

	/**
	 * @param headers
	 * @param accountNumber
	 * @param transactionRequest
	 * @return
	 */
	@PostMapping(value = "${transaction.service.txn.entry.path}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<TransactionResponse> entry(@RequestHeader HttpHeaders headers,@PathVariable("accountnumber") String accountNumber,@RequestBody TransactionRequest transactionRequest);
}
