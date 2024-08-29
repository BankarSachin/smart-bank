package com.smartbank.transactionservice.mapper;

import com.smartbank.transactionservice.dto.TransactionRequest;
import com.smartbank.transactionservice.dto.TransferRequest;
import com.smartbank.transactionservice.entity.Transaction;
import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.entity.external.Account;
import com.smartbank.transactionservice.enums.TransactionType;

public class TransactionEntryMapper {
	

	public static TransactionEntry toEntity(Transaction transaction,TransactionRequest transactionRequest, String accountNumber) {
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setUtrNumber(transaction.getUtrNumber());	
		transactionEntry.setTransactionType(transactionRequest.getTransactionType());
		transactionEntry.setTransactionAmount(transactionRequest.getTransactionAmount());
		transactionEntry.setAccountNumber(accountNumber);	
		transactionEntry.setClosingBalance(transactionRequest.getClosingBalance());
		transactionEntry.setTransaction(transaction);
		return transactionEntry;
	}
	
	public static TransactionEntry toEntity(Transaction transaction,Account account,TransferRequest transferRequest,TransactionType transactionType) {
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setUtrNumber(transaction.getUtrNumber());	
		transactionEntry.setTransactionType(transactionType);
		transactionEntry.setTransactionAmount(transferRequest.getTransactionAmount());
		transactionEntry.setAccountNumber(account.getAccountNumber());	
		transactionEntry.setClosingBalance(account.getCurrentBalance());
		transactionEntry.setTransaction(transaction);
		return transactionEntry;
	}
}
