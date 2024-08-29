package com.smartbank.accountservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smartbank.accountservice.entity.Account;

/**
 * Repository to handle account related DB interactions
 * @author Sachin
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

	 @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
     Long getNextSequenceValue();
	 
	 Optional<Account> findByAccountNumber(String accountNumber);
}
