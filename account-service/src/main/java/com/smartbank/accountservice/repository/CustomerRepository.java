package com.smartbank.accountservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbank.accountservice.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

	Optional<Customer> findByCustomerId(Long customerId);
	
	Optional<Customer> findByEmail(String email);
	/**
	 * @param email
	 * @return
	 */
	boolean existsByEmail(String email);
	
	/**
	 * @param phoneNumber
	 * @return
	 */
	boolean existsByPhoneNumber(String phoneNumber);
}
