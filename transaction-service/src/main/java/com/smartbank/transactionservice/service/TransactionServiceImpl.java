package com.smartbank.transactionservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartbank.transactionservice.dto.TransactionRequest;
import com.smartbank.transactionservice.dto.TransactionResponse;
import com.smartbank.transactionservice.entity.Transaction;
import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.enums.TransactionType;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;
import com.smartbank.transactionservice.mapper.TransactionEntryMapper;
import com.smartbank.transactionservice.mapper.TransactionMapper;
import com.smartbank.transactionservice.repository.TransactionEntryRepository;
import com.smartbank.transactionservice.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private TransactionEntryRepository transactionEntryRepository;
	
	@Override
	@Transactional
	public TransactionResponse entry(String accountNumber, TransactionRequest transactionRequest) throws TxnException {
		final String methodName = "entry";
		try {
				Transaction transaction = TransactionMapper.toEntity(transactionRequest);;
				transaction = transactionRepository.save(transaction);
				
				TransactionEntry  transactionEntry = TransactionEntryMapper.toEntity(transaction, transactionRequest,accountNumber);
				
				transaction.setTransactionEntries(List.of(transactionEntry));
				
				transactionEntry = transactionEntryRepository.save(transactionEntry);
				
				return TransactionMapper.toResponse(transaction, transactionEntry);
				
		} catch (Exception e) {
			log.error("{} - Unknown Error observerd during transaction entry creation {}", methodName,e.getMessage());
			throw new TxnException(ExceptionCode.TXNS_UNKNOWN_EXCEPTION, e);
		}
				
	}

	@Override
	public List<TransactionResponse> getTxHistory(String accountNumber, LocalDate startDate, LocalDate endDate,
			TransactionType transactionType) throws TxnException {
		final String methodName = "getTxHistory";
		try {
				List<TransactionEntry> transactions =  transactionRepository.findTransactionsByCriteria(accountNumber, 
																								   startDate, 
																								   endDate, 
																								   transactionType
																								  );
				return transactions.stream()
						.map(TransactionMapper.txnResponseMapper :: apply)
						.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("{} - Error observerd during transaction history fetch {}", methodName,e.getMessage());
			throw new TxnException(ExceptionCode.TXNS_UNKNOWN_EXCEPTION, e);
		}
	}

}
