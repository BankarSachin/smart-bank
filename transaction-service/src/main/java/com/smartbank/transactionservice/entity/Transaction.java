package com.smartbank.transactionservice.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.smartbank.transactionservice.enums.TransactionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "utr_number")
	private UUID utrNumber;
	
	@Column(name="transaction_summary")
	private String transactionSummary;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_status", nullable = false, length = 15)
	private TransactionStatus transactionStatus;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "transaction_date", nullable = false)
	private LocalDateTime transactionDate;
	
	@OneToMany(mappedBy = "transaction",cascade = CascadeType.ALL)
	private List<TransactionEntry> transactionEntries;

}
