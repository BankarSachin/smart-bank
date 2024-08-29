package com.smartbank.notificationservice.repository.external;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbank.notificationservice.entity.external.Account;

/**
 * Repository to handle account related DB interactions
 * @author Sachin
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	 Optional<Account> findByAccountNumber(String accountNumber);
}
