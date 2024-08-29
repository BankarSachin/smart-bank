package com.smartbank.notificationservice.entity.external;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

/**
 * Holds customer information.
 * For simplicity we are having one to to one mapping between {@link Account} and {@link Customer}
 * <p>
 * In real world scenario it would be one customer and many accounts i.e. One to Many mapping
 * @author Sachin
 *
 */
@Entity
@Data
@Table(name = "customers")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Long customerId;
	
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;
	
	@Column(name = "phone_number", nullable = false, unique = true, length = 15)
	private String phoneNumber;
	
	@Column(name = "password", nullable = false, length = 500)
	private String password;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createDate;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;
	
	@OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	private List<Account> account;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "customer_permission_mappings",
		joinColumns = @JoinColumn(name="customer_id"),
		inverseJoinColumns = @JoinColumn(name="permission_id")
	)
	private List<Permissions> customerPermissions; 
}
