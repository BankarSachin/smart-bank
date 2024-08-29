package com.smartbank.transactionservice.service;

import java.time.LocalDate;
import java.util.List;

import com.smartbank.transactionservice.dto.TransactionRequest;
import com.smartbank.transactionservice.dto.TransactionResponse;
import com.smartbank.transactionservice.enums.TransactionType;
import com.smartbank.transactionservice.exception.TxnException;

/** Perfroms transaction related activity
 * @author Sachin
 */
public interface TransactionService {

	/**
	 * Commits transaction data sent by other service
	 * @param transactionDTO
	 * @return
	 * @throws TxnException
	 */
	TransactionResponse entry(String accountNumber,TransactionRequest transactionRequest) throws TxnException;
	
	/**Gives back transaction History
	 * @param accountNumber
	 * @return
	 * @throws TxnException
	 */
	List<TransactionResponse> getTxHistory(String accountNumber, LocalDate startDate, LocalDate endDate,
			TransactionType transactionType) throws TxnException;
	
}
