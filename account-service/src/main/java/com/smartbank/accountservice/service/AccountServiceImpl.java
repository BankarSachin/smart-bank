/**
 * 
 */
package com.smartbank.accountservice.service;

import static com.smartbank.accountservice.mapper.AccountMapper.toTxnEntity;
import static com.smartbank.accountservice.mapper.ReponseMappers.txnToDepositRespMapper;
import static com.smartbank.accountservice.mapper.ReponseMappers.txnToWithdrawalRespMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.accountservice.dto.AccountTransaction;
import com.smartbank.accountservice.dto.BalanceReponse;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.DepositResponse;
import com.smartbank.accountservice.dto.NotificationRequest;
import com.smartbank.accountservice.dto.NotificationResponse;
import com.smartbank.accountservice.dto.TransactionResponse;
import com.smartbank.accountservice.dto.WithdrawalResponse;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.NotificationType;
import com.smartbank.accountservice.enums.TransactionType;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.mapper.AccountMapper;
import com.smartbank.accountservice.repository.AccountRepository;
import com.smartbank.accountservice.service.external.NotificationServiceClient;
import com.smartbank.accountservice.service.external.TransactionServiceClient;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles Account Related Operations
 *  1> Account Creation
 *  2> Deposit Account
 *  3> Withdrawal
 *  4> Balance Inquiry
 * @author Sachin
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TransactionServiceClient transactionServiceClient;
	
	@Autowired
	private NotificationServiceClient notificationServiceClient;
	
	/**
	 * Creates Customer
	 * Creates Corresponding Account
	 * It is not best practice to create zero balance account with registration. But for MVP this has been done
	 * Better flow would be
	 * 1> Register customer
	 * 2> User Log into system and sends request to create account
	 * @throws AccsException 
	 */
	@Override
	@Transactional
	public Account createAccount(Customer customer,CustomerAccountDTO customerDto) throws AccsException {
		final String methodName = "createAccount";
		try {
			final String accountNumber = generateAccountNumber(customerDto.getBranchCode());
			return accountRepository.save(AccountMapper.toEntity(customer, accountNumber, customerDto));
		} catch (Exception e) {
			log.error("{} - error {}",methodName,e.getMessage(),e);
			throw new AccsException(ExceptionCode.ACCS_UNKNOWN_EXCEPTION, e);
		}
	}

	
	/**
	 * Generates Account number which is Fixed Length of 10
	 * @param branchCode
	 * @return
	 */
	private String generateAccountNumber(String branchCode) {
		Long accountSequenceNumber = accountRepository.getNextSequenceValue();
		String strAccountSeqNumber = String.valueOf(accountSequenceNumber);
		final String branchCodepart = branchCode.substring(branchCode.length()-4, branchCode.length()); 
		if (strAccountSeqNumber.length()>6) {
			return branchCodepart + strAccountSeqNumber.substring(strAccountSeqNumber.length()-6, strAccountSeqNumber.length());
		}
		return branchCodepart + String.format("%06d", accountSequenceNumber);
	}


	/**
	 *By Default transaction rollbacks on Runtime and Error. Adding these two would make it rollback for 4 conditions
	 * AccsException,Exception and Default ones
	 *
	 * There is one drawback when supposse transaction has been commited 
	 *
	 */
	@Override
	@Transactional(rollbackOn = {AccsException.class,Exception.class})
	public DepositResponse deposit(Map<String, String> headers,String accounNumber, AccountTransaction accountTransaction) throws AccsException {
		final String methodName = "deposit";
		try {
			
			Account account = accountRepository.findByAccountNumber(accounNumber).orElseThrow(()-> new AccsException(ExceptionCode.ACC_ACCOUNT_NON_EXIST));
			
			if (account.getAccountStatus()!=AccountStatus.ACTIVE) {
				throw new AccsException(ExceptionCode.ACC_ACCOUNT_STATUS_INVALID);
			}
			final BigDecimal newBalance = account.getCurrentBalance().add(accountTransaction.getTransactionAmount());
			account.setCurrentBalance(newBalance);
			account = accountRepository.save(account);
			
			//Commit Transaction
			TransactionResponse transactionResponse =  transactionServiceClient.crateTxnEntry(headers, 
																							  accounNumber, 
																							  toTxnEntity(account, accountTransaction, TransactionType.CREDIT)
																							  );
			log.info("{} - Deposit successful for {}. UTR number {}",methodName,accounNumber, transactionResponse.getUtrNumber());
			sendNotification(headers, transactionResponse, accounNumber, NotificationType.CREDIT);
			return txnToDepositRespMapper.apply(transactionResponse);
			
		} catch (AccsException e) {
			log.error("{} - Error occured while deposit flow {}", methodName,e.getMessage());
			//call to transaction rollback - Asynch goes here
			throw e;
		} catch (Exception e) {
			log.error("{} - Error occured while deposit flow {}", methodName,e.getMessage(),e);
			//call to transaction rollback - Asynch goes here
			throw new AccsException(ExceptionCode.ACC_ACCOUNT_DEPOSIT_UNKNOWN_EXCEPTION, e);
		}
	}


	@Override
	@Transactional
	public WithdrawalResponse withdrawal(Map<String, String> headers,String accounNumber, AccountTransaction accountTransaction) throws AccsException {
		final String methodName = "withdrawal";
		try {
			
			Account account = accountRepository.findByAccountNumber(accounNumber).orElseThrow(()-> new AccsException(ExceptionCode.ACC_ACCOUNT_NON_EXIST));
			
			if (account.getAccountStatus()!=AccountStatus.ACTIVE) {
				throw new AccsException(ExceptionCode.ACC_ACCOUNT_STATUS_INVALID);
			}
			
			final BigDecimal currentBalance = account.getCurrentBalance();
			
			//It won't change current balanec. BigDecimal is immutable one like String
			if(currentBalance.compareTo(accountTransaction.getTransactionAmount()) < 0) {
				throw new AccsException(ExceptionCode.ACCS_INSUFFICIENT_BALANCE_EXCEPTION);
			}

			final BigDecimal newBalance = account.getCurrentBalance().subtract(accountTransaction.getTransactionAmount());
			account.setCurrentBalance(newBalance);
			account = accountRepository.save(account);
			
			TransactionResponse transactionResponse =  transactionServiceClient.crateTxnEntry(headers, 
					                                                                          accounNumber, 
					                                                                          toTxnEntity(account, accountTransaction, TransactionType.DEBIT)
					                                                                          );
			log.info("{} - Deposit successful for {}. UTR number {}",methodName,accounNumber, transactionResponse.getUtrNumber());
			sendNotification(headers, transactionResponse, accounNumber, NotificationType.DEBIT);
			return txnToWithdrawalRespMapper.apply(transactionResponse);
			
		} catch (AccsException e) {
			log.error("{} - Error occured while withdrawal flow {}", methodName,e.getMessage());
			throw e;
			
			//call to transaction entry rollback - Asynch goes here
		} catch (Exception e) {
			log.error("{} - Error occured while withdrawal flow {}", methodName,e.getMessage(),e);
			//call to transaction entry rollback - Asynch goes here
			throw new AccsException(ExceptionCode.ACC_ACCOUNT_WITHDRAWAL_UNKNOWN_EXCEPTION, e);
		}
	}


	/**
	 * CHeck balance of Account.
	 * Exception for Inactive or suspended accounts
	 */
	@Override
	public BalanceReponse balance(String accounNumber) throws AccsException {
		final String methodName = "withdrawal";
		try {
			
			Account account = accountRepository.findByAccountNumber(accounNumber).orElseThrow(()-> new AccsException(ExceptionCode.ACC_ACCOUNT_NON_EXIST));
			
			if (account.getAccountStatus()!=AccountStatus.ACTIVE) {
				throw new AccsException(ExceptionCode.ACC_ACCOUNT_STATUS_INVALID);
			}
			
			return new BalanceReponse(accounNumber,account.getCurrentBalance());
			
		} catch (AccsException e) {
			log.error("{} - Error occured while balance check {}", methodName,e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("{} - Error occured while balance check {}", methodName,e.getMessage(),e);
			throw new AccsException(ExceptionCode.ACC_ACCOUNT_WITHDRAWAL_UNKNOWN_EXCEPTION, e);
		}
	}
	
	/**
	 * Sent notification mail in Asynchronous fashion
	 * @param transaction
	 * @param debitAccount
	 * @param creditAccount
	 * @param transferRequest
	 */
	private void sendNotification(Map<String,String> headers,TransactionResponse transaction, String accountNumber,NotificationType notificationType) {
		
		NotificationRequest notificationRequest = NotificationRequest.builder()
												  .notificationType(notificationType)
												  .txnAmmount(transaction.getTransactionAmount())
												  .txnDateTime(transaction.getTransactionDate())
												  .currentBalance(transaction.getClosingBalance())
												  .utrNumber(transaction.getUtrNumber())
												  .build();
												  
		CompletableFuture<NotificationResponse> notificationFuture = notificationServiceClient.notifyTransfer(headers,accountNumber,notificationRequest);
		String notificationResponse = notificationFuture
										.thenApply(Object ::toString)
										.exceptionally(e->e.getMessage())
										.join();
		log.info("sendNotification - account service received response from notificationservice {}", notificationResponse);
	}
}
