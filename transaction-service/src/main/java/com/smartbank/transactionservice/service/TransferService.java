package com.smartbank.transactionservice.service;

import java.util.List;
import java.util.Map;

import com.smartbank.transactionservice.dto.TransactionResponse;
import com.smartbank.transactionservice.dto.TransferRequest;
import com.smartbank.transactionservice.exception.TxnException;

public interface TransferService {


	/**
	 * Perfrom transfer between two accounts
	 * @param transferRequest
	 * @return
	 * @throws TxnException
	 */
	List<TransactionResponse> performTransfer(Map<String,String> headers,String accountNumber,TransferRequest transferRequest) throws TxnException;
}
