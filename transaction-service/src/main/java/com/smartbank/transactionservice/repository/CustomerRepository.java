package com.smartbank.transactionservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbank.transactionservice.entity.external.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

	Optional<Customer> findByCustomerId(Long customerId);
	
	Optional<Customer> findByEmail(String email);
}
