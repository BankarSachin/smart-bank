package com.smartbank.accountservice.service;

import java.util.Map;

import com.smartbank.accountservice.dto.AccountTransaction;
import com.smartbank.accountservice.dto.BalanceReponse;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.DepositResponse;
import com.smartbank.accountservice.dto.WithdrawalResponse;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.exception.AccsException;

public interface AccountService {

	/**
	 * creates customer in DB and creates 
	 * @param customerDto
	 * @throws {@link AccsException} if user already exists
	 * @return
	 */
	public Account createAccount(Customer customer,CustomerAccountDTO customerDto) throws AccsException;
	
	/**
	 * Deposits given ammout to use 
	 * @param accounNumber
	 * @param depositAmmounr
	 * @return
	 * @throws AccsException
	 */
	public DepositResponse deposit(Map<String, String> headers,String accounNumber,AccountTransaction transactionRequest) throws AccsException;
	
	
	/**
	 * Deposits given ammout to use 
	 * @param accounNumber
	 * @param depositAmmounr
	 * @return
	 * @throws AccsException
	 */
	public WithdrawalResponse withdrawal(Map<String, String> headers,String accounNumber,AccountTransaction transactionRequest) throws AccsException;
	
	/**
	 * Gives Back Current Balance 
	 * @param accounNumber
	 * @return
	 * @throws AccsException
	 */
	public BalanceReponse balance(String accounNumber) throws AccsException;
}
