package com.smartbank.transactionservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.transactionservice.dto.NotificationRequest;
import com.smartbank.transactionservice.dto.NotificationResponse;
import com.smartbank.transactionservice.dto.TransactionResponse;
import com.smartbank.transactionservice.dto.TransferRequest;
import com.smartbank.transactionservice.entity.Transaction;
import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.entity.external.Account;
import com.smartbank.transactionservice.enums.AccountStatus;
import com.smartbank.transactionservice.enums.NotificationType;
import com.smartbank.transactionservice.enums.TransactionStatus;
import com.smartbank.transactionservice.enums.TransactionType;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;
import com.smartbank.transactionservice.mapper.TransactionEntryMapper;
import com.smartbank.transactionservice.mapper.TransactionMapper;
import com.smartbank.transactionservice.repository.TransactionEntryRepository;
import com.smartbank.transactionservice.repository.TransactionRepository;
import com.smartbank.transactionservice.repository.external.AccountRepository;
import com.smartbank.transactionservice.service.external.NotificationServiceClient;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService{

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private TransactionEntryRepository transactionEntryRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private NotificationServiceClient notificationServiceClient;

	@Override
	@Transactional(rollbackOn = {TxnException.class,Exception.class})
	public List<TransactionResponse> performTransfer(Map<String,String> headers,String accountNumber,TransferRequest transferRequest) throws TxnException {
		final String methodName = "entry";
		try {
				final BigDecimal ammount = transferRequest.getTransactionAmount();
				Account debitAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new TxnException(ExceptionCode.TXNS_SRC_ACCOUNT_NON_EXISTS, accountNumber));
				
				if (debitAccount.getAccountStatus()!=AccountStatus.ACTIVE) {
					throw new TxnException(ExceptionCode.TXNS_SRC_ACCOUNT_NON_ACTIVE,accountNumber);
				}
				
				//-1 = this < input
				//0 = this equal input
				//1 = this > input
				if (debitAccount.getCurrentBalance().compareTo(ammount) < 0) {
					throw new TxnException(ExceptionCode.ACCS_INSUFFICIENT_BALANCE_EXCEPTION,accountNumber);
				}
				
				Account creditAccount = accountRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber()).orElseThrow(()-> new TxnException(ExceptionCode.TXNS_DEST_ACCOUNT_NON_EXISTS, transferRequest.getDestinationAccountNumber()));
			
				if (debitAccount.getAccountStatus()!=AccountStatus.ACTIVE) {
					throw new TxnException(ExceptionCode.TXNS_DEST_ACCOUNT_NON_ACTIVE,transferRequest.getDestinationAccountNumber());
				}
				
				debitAccount.setCurrentBalance(debitAccount.getCurrentBalance().subtract(ammount));
				creditAccount.setCurrentBalance(creditAccount.getCurrentBalance().add(ammount));
				
				debitAccount = accountRepository.save(debitAccount);
				creditAccount = accountRepository.save(creditAccount);
				
				log.info("{} - Source account debited and Destination account credited",methodName);
				
				Transaction transaction = new Transaction();
				transaction.setTransactionStatus(TransactionStatus.SUCCESS);
				transaction.setTransactionDate(debitAccount.getUpdateddDate());
				transaction.setTransactionSummary(transferRequest.getTransactionSummary());
				
				transaction = transactionRepository.save(transaction);
				
				log.info("{} - Transaction created",methodName);
				
				List<TransactionEntry>  transactionEntries = List.of(
						TransactionEntryMapper.toEntity(transaction, debitAccount, transferRequest, TransactionType.DEBIT),
						TransactionEntryMapper.toEntity(transaction, creditAccount, transferRequest, TransactionType.CREDIT)
						);
				
				transactionEntries  = transactionEntryRepository.saveAll(transactionEntries);
				log.info("{} - Transaction entries added for debit and credit account",methodName);
				List<TransactionResponse> txnResult = new ArrayList<>();
				for (TransactionEntry entry : transactionEntries) {
					txnResult.add(TransactionMapper.toResponse(transaction, entry));
				}
				sendNotification(headers,transaction,debitAccount,creditAccount,transferRequest);
				return txnResult;
				
		} catch (TxnException e) {
			log.error("{} - Transaction Error observerd during fund transfer {}", methodName,e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("{} - Unknown Error observerd during fund transfe {}", methodName,e.getMessage());
			throw new TxnException(ExceptionCode.TXNS_UNKNOWN_EXCEPTION, e);
		}
	}

	/**
	 * Sent notification mail in Asynchronous fashion
	 * @param transaction
	 * @param debitAccount
	 * @param creditAccount
	 * @param transferRequest
	 */
	private void sendNotification(Map<String,String> headers,Transaction transaction, Account debitAccount, Account creditAccount,
			TransferRequest transferRequest) {
		
		NotificationRequest notificationRequest = NotificationRequest.builder()
												  .notificationType(NotificationType.TRANSFER)
												  .destinationAccountNumber(creditAccount.getAccountNumber())
												  .destinationCurrentBalance(creditAccount.getCurrentBalance())
												  .txnAmmount(transferRequest.getTransactionAmount())
												  .txnDateTime(transaction.getTransactionDate())
												  .currentBalance(debitAccount.getCurrentBalance())
												  .utrNumber(transaction.getUtrNumber())
												  .build();
												  
		CompletableFuture<NotificationResponse> notificationFuture = notificationServiceClient.notifyTransfer(headers
																				, debitAccount.getAccountNumber(),notificationRequest);
		String notificationResponse = notificationFuture
										.thenApply(Object ::toString)
										.exceptionally(e->e.getMessage())
										.join();
		log.info("sendNotification - response from notificationservice {}", notificationResponse);
	}

}
