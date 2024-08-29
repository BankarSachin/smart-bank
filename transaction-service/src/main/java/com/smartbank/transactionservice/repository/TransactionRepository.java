package com.smartbank.transactionservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartbank.transactionservice.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>,TransactionRepositoryCustom{

}
