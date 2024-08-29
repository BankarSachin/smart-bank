package com.smartbank.transactionservice.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.smartbank.transactionservice.entity.Transaction;
import com.smartbank.transactionservice.entity.TransactionEntry;
import com.smartbank.transactionservice.enums.TransactionType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param accountNumber 
     * @param startDate fromat is yyyy-MM-dd (the ISO 8601 format)
     * @param endDate fromat is yyyy-MM-dd (the ISO 8601 format)
     * @param transactionType type of transaction needs to be fetched
     */
    @Override
    public List<TransactionEntry> findTransactionsByCriteria(String accountNumber, LocalDate startDate, LocalDate endDate, TransactionType transactionType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TransactionEntry> cq = cb.createQuery(TransactionEntry.class);
        Root<TransactionEntry> transactionEntryRoot = cq.from(TransactionEntry.class);
        Join<TransactionEntry,Transaction> transactionJoin = transactionEntryRoot.join("transaction");
        

        List<Predicate> predicates = new ArrayList<>();

        // Mandatory predicate: account number
        predicates.add(cb.equal(transactionEntryRoot.get("accountNumber"), accountNumber));

        // Optional: start date
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(transactionJoin.get("transactionDate"), startDate.atStartOfDay()));
        }

        // Optional: end date
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(transactionJoin.get("transactionDate"), endDate.atTime(23, 59, 59)));
        }

        // Optional: transaction type
        if (transactionType != null) {
            predicates.add(cb.equal(transactionEntryRoot.get("transactionType"), transactionType));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<TransactionEntry> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
