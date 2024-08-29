package com.smartbank.accountservice.dto;

import java.math.BigDecimal;

import com.smartbank.accountservice.enums.AccountType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO class to carry data from controller to services and repository
 * @author Sachin
 *
 */
@Data
public class CustomerAccountDTO {
	
	@NotBlank(message = "Name is required")
	@Size(max = 100, message = "Name must be at most 100 characters long")
    private String name;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	@Size(max = 100, message = "Email must be at most 100 characters long")
    private String email;
	
	@NotBlank(message = "Phone number is required")
	@Size(max = 15, message = "Phone number must be at most 15 characters long")
    private String phoneNumber;
	
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 500, message = "Password must be between 8 and 500 characters long")
    private String password;
	
	@NotNull(message = "Account type is required")
	private AccountType accountType;
	
	@NotNull(message = "Branch Code type is required")
	@Pattern(regexp = "^[A-Za-z]{4}[0-9]{7}$",message = "Branch code should be 11 digit long and Alphanumeric")
	private String branchCode;
	
	@Digits(integer = 10, fraction = 2, message = "Please enter a valid amount with up to 10 digits before the decimal and up to 2 digits after the decimal.")
	private BigDecimal amount;
}
