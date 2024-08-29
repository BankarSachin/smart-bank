package com.smartbank.transactionservice.repository;

import java.time.LocalDate;
import java.util.List;

import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.enums.TransactionType;

public interface TransactionRepositoryCustom {
    List<TransactionEntry> findTransactionsByCriteria(String accountNumber, LocalDate startDate, LocalDate endDate, TransactionType transactionType);
}