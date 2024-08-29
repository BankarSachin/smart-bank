package com.smartbank.transactionservice.mapper;

import java.util.function.Function;

import com.smartbank.transactionservice.dto.TransactionRequest;
import com.smartbank.transactionservice.dto.TransactionResponse;
import com.smartbank.transactionservice.entity.Transaction;
import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.enums.TransactionStatus;

public class TransactionMapper {

	public static Transaction toEntity(TransactionRequest transactionRequest) {
		Transaction transaction = new Transaction();
		transaction.setTransactionDate(transactionRequest.getTransactionDate());
		transaction.setTransactionStatus(TransactionStatus.SUCCESS);
		transaction.setTransactionSummary(transactionRequest.getTransactionSummary());
		return transaction;
	}

	public static TransactionResponse toResponse(Transaction transaction, TransactionEntry transactionEntry) {
		TransactionResponse transactionResponse = new TransactionResponse();
		transactionResponse.setUtrNumber(transaction.getUtrNumber());
		transactionResponse.setTransactionType(transactionEntry.getTransactionType());
		transactionResponse.setTransactionStatus(transaction.getTransactionStatus());
		transactionResponse.setTransactionDate(transaction.getTransactionDate());
		transactionResponse.setTransactionAmount(transactionEntry.getTransactionAmount());
		transactionResponse.setTransactionAccount(transactionEntry.getAccountNumber());
		transactionResponse.setClosingBalance(transactionEntry.getClosingBalance());
		transactionResponse.setTransactionDescription(transaction.getTransactionSummary());
		return transactionResponse;
	}

	public static final Function<TransactionEntry, TransactionResponse> txnResponseMapper = transactionEntry -> {
		TransactionResponse transactionResponse = new TransactionResponse();
		
		transactionResponse.setUtrNumber(transactionEntry.getUtrNumber());
		transactionResponse.setTransactionAccount(transactionEntry.getAccountNumber());
		transactionResponse.setTransactionAmount(transactionEntry.getTransactionAmount());
		transactionResponse.setTransactionType(transactionEntry.getTransactionType());
		
		Transaction txn = transactionEntry.getTransaction();
		transactionResponse.setTransactionDate(txn.getTransactionDate());
		transactionResponse.setTransactionDescription(txn.getTransactionSummary());
		transactionResponse.setClosingBalance(transactionEntry.getClosingBalance());
		transactionResponse.setTransactionStatus(txn.getTransactionStatus());
		transactionResponse.setTransactionDescription(txn.getTransactionSummary());
		return transactionResponse;
	};
}
