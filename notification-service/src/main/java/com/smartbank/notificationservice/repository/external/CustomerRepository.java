package com.smartbank.notificationservice.repository.external;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbank.notificationservice.entity.external.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

	Optional<Customer> findByCustomerId(Long customerId);
	
	Optional<Customer> findByEmail(String email);
}
