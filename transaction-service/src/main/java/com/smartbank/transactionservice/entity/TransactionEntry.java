package com.smartbank.transactionservice.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.smartbank.transactionservice.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transaction_entries")
@Data
public class TransactionEntry {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_entry_id", updatable = false, nullable = false)
    private UUID transactionEntryId;

    @Column(name = "utr_number", nullable = false)
    private UUID utrNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 2)
    private TransactionType transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "closing_balance", nullable = false)
    private BigDecimal closingBalance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utr_number", insertable = false, updatable = false)
    private Transaction transaction;

    @Column(name="account_number",nullable = false)
    private String accountNumber;
}
