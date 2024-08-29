package com.smartbank.transactionservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbank.transactionservice.entity.TransactionEntry;

@Repository
public interface TransactionEntryRepository extends JpaRepository<TransactionEntry, UUID>{

}
