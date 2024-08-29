package com.smartbank.accountservice.mapper;

import java.math.BigDecimal;

import com.smartbank.accountservice.dto.AccountTransaction;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.TransactionRequest;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.TransactionType;

public class AccountMapper {
	
	/**
	 * Private Constructor
	 */
	private AccountMapper() {
		//Private Constructor
	}
	
	public static Account toEntity(Customer customer,String accountNumber,CustomerAccountDTO customerDto) {
		Account account = new Account();
		account.setAccountNumber(accountNumber);
		account.setAccountStatus(AccountStatus.ACTIVE);
		account.setAccountType(customerDto.getAccountType());
		account.setBranchCode(customerDto.getBranchCode());
		account.setCustomer(customer);
		account.setCurrentBalance(customerDto.getAmount() == null ? BigDecimal.ZERO : customerDto.getAmount());
		return account;
	}
	
	/**
	 * Create entity for Ledger Entry or Transaction Entry
	 * @param account
	 * @param accountTransaction
	 * @param transactionType
	 * @return
	 */
	public static TransactionRequest toTxnEntity(Account account,AccountTransaction accountTransaction,TransactionType transactionType) {
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setTransactionAmount(accountTransaction.getTransactionAmount());
		transactionRequest.setClosingBalance(account.getCurrentBalance());
		transactionRequest.setTransactionSummary(accountTransaction.getTransactionSummary());
		transactionRequest.setTransactionType(transactionType);
		transactionRequest.setTransactionDate(account.getUpdateddDate());
		return transactionRequest;
	}
}
