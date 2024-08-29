package com.smartbank.notificationservice.entity.external;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permissions {
	@Id
	@Column(name = "permission_id")
	private String permissionId;
	
	@Column(name = "permission")
	private String permission;
	
	@ManyToMany(mappedBy = "customerPermissions")
	List<Customer> customers;
}
