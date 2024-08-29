package com.smartbank.accountservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartbank.accountservice.dto.AccountTransaction;
import com.smartbank.accountservice.dto.BalanceReponse;
import com.smartbank.accountservice.dto.CustomerAccountDTO;
import com.smartbank.accountservice.dto.DepositResponse;
import com.smartbank.accountservice.dto.NotificationRequest;
import com.smartbank.accountservice.dto.NotificationResponse;
import com.smartbank.accountservice.dto.TransactionResponse;
import com.smartbank.accountservice.dto.WithdrawalResponse;
import com.smartbank.accountservice.entity.Account;
import com.smartbank.accountservice.entity.Customer;
import com.smartbank.accountservice.enums.AccountStatus;
import com.smartbank.accountservice.enums.AccountType;
import com.smartbank.accountservice.exception.AccsException;
import com.smartbank.accountservice.exception.ExceptionCode;
import com.smartbank.accountservice.mapper.AccountMapper;
import com.smartbank.accountservice.repository.AccountRepository;
import com.smartbank.accountservice.service.external.NotificationServiceClient;
import com.smartbank.accountservice.service.external.TransactionServiceClient;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class AccountServiceTest {

	public static final String UTR_NUMBER = "123e4567-e89b-12d3-a456-426614174000";

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionServiceClient transactionServiceClient;

	@Mock
	private NotificationServiceClient notificationServiceClient;

	@InjectMocks
	private AccountServiceImpl accountService;

	private Map<String, String> headers;
	private String accountNumber;
	private AccountTransaction accountTransaction;
	private Account account;
	private TransactionResponse transactionResponse;
	private CompletableFuture<NotificationResponse> notificationCompletableFuture;
	private CustomerAccountDTO customerAccountDto;
	private Customer customer;
	
	@BeforeEach
	void setUp() {
		headers = Map.of("Authorization", "Bearer token");
		accountNumber = "0988567890";
		accountTransaction = new AccountTransaction();
		accountTransaction.setTransactionAmount(BigDecimal.valueOf(1000));

		account = new Account();
		account.setAccountNumber(accountNumber);
		account.setAccountStatus(AccountStatus.ACTIVE);
		account.setCurrentBalance(BigDecimal.valueOf(5000));

		transactionResponse = new TransactionResponse();
		transactionResponse.setUtrNumber(UUID.fromString(UTR_NUMBER));
		transactionResponse.setTransactionAmount(BigDecimal.valueOf(1000));

		customerAccountDto = new CustomerAccountDTO();
		customerAccountDto.setAccountType(AccountType.SAVINGS);
		customerAccountDto.setBranchCode("ABCD0000988");
		
		NotificationResponse notificationResponse = new NotificationResponse("Success", "Email sent");
		notificationCompletableFuture = CompletableFuture.completedFuture(notificationResponse);

		customer = new Customer();
	}

	@Test
	@Order(1)
	void testCreateAccount() throws AccsException{
		
		Account zeroBalanceAccount = AccountMapper.toEntity(customer, accountNumber, customerAccountDto);
		
		//arrange
		when(accountRepository.getNextSequenceValue()).thenReturn(1234567890L);
		when(accountRepository.save(zeroBalanceAccount)).thenReturn(zeroBalanceAccount);
		
		//act
		Account account1 =  accountService.createAccount(customer, customerAccountDto);
		assertEquals(AccountType.SAVINGS, account1.getAccountType());
		
		//verify
		verify(accountRepository,times(1)).getNextSequenceValue();
		verify(accountRepository,times(1)).save(zeroBalanceAccount);
	}
	
	@Test
	@Order(2)
	void testDeposit() throws AccsException {
		accountTransaction.setTransactionSummary("Depositing 1000");
		transactionResponse.setClosingBalance(BigDecimal.valueOf(6000));

		// arrage
		when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
		when(transactionServiceClient.crateTxnEntry(eq(headers), eq(accountNumber), any())).thenReturn(transactionResponse);
		when(accountRepository.save(account)).thenReturn(account);
		when(notificationServiceClient.notifyTransfer(eq(headers), eq(accountNumber), any())).thenReturn(notificationCompletableFuture);
		
		// act
		DepositResponse response = accountService.deposit(headers, accountNumber, accountTransaction);

		// asert
		assertEquals(UUID.fromString(UTR_NUMBER), response.utrNumber());
		assertEquals(BigDecimal.valueOf(6000), response.newBalance());

		verify(accountRepository, times(1)).save(account);
		verify(transactionServiceClient, times(1)).crateTxnEntry(eq(headers), eq(accountNumber), any());
		verify(notificationServiceClient, times(1)).notifyTransfer(eq(headers), eq(accountNumber),any(NotificationRequest.class));
	}

	@Test
	@Order(3)
	void testInactiveAccount() throws AccsException{
		account.setAccountStatus(AccountStatus.CLOSED);
		when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
		
		AccsException ex =  assertThrows(AccsException.class, () -> accountService.deposit(headers, accountNumber, accountTransaction));
		assertEquals(ExceptionCode.ACC_ACCOUNT_STATUS_INVALID,ex.getExceptionCode());
		
		verify(accountRepository,never()).save(account);
		verify(transactionServiceClient, never()).crateTxnEntry(any(), any(), any());
		verify(notificationServiceClient, never()).notifyTransfer(any(), any(), any());
	}
	
	@Test
	@Order(4)
    void testDepositAccountNotFound()  throws AccsException {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(AccsException.class, () -> accountService.deposit(headers, accountNumber, accountTransaction));

        verify(accountRepository, never()).save(any());
        verify(transactionServiceClient, never()).crateTxnEntry(any(), any(), any());
        verify(notificationServiceClient, never()).notifyTransfer(any(), any(), any());
    }

	@Test
	@Order(4)
	void testWithdrawal() throws AccsException {
		accountTransaction.setTransactionSummary("Withdrawing 1000");
		transactionResponse.setClosingBalance(BigDecimal.valueOf(4000));
		// Arrange
		when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
		when(transactionServiceClient.crateTxnEntry(eq(headers), eq(accountNumber), any())).thenReturn(transactionResponse);
		when(accountRepository.save(account)).thenReturn(account);
		when(notificationServiceClient.notifyTransfer(eq(headers), eq(accountNumber), any())).thenReturn(notificationCompletableFuture);

		WithdrawalResponse response = accountService.withdrawal(headers, accountNumber, accountTransaction);

		assertEquals(UUID.fromString(UTR_NUMBER), response.utrNumber());
		assertEquals(BigDecimal.valueOf(4000), response.newBalance());

		verify(accountRepository).save(account);
		verify(transactionServiceClient).crateTxnEntry(eq(headers), eq(accountNumber), any());
		verify(notificationServiceClient).notifyTransfer(eq(headers), eq(accountNumber),any(NotificationRequest.class));
	}

	@Test
	@Order(6)
	void testWithdrawalInsufficientBalance() throws AccsException{
		account.setCurrentBalance(BigDecimal.valueOf(500));
		when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

		AccsException ex = assertThrows(AccsException.class,() -> accountService.withdrawal(headers, accountNumber, accountTransaction));
		assertEquals(ExceptionCode.ACCS_INSUFFICIENT_BALANCE_EXCEPTION, ex.getExceptionCode());

		verify(accountRepository, never()).save(any());
		verify(transactionServiceClient, never()).crateTxnEntry(any(), any(), any());
		verify(notificationServiceClient, never()).notifyTransfer(any(), any(), any());
	}

	@Test
	@Order(7)
    void testBalance() throws AccsException {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        BalanceReponse response = accountService.balance(accountNumber);

        assertEquals(accountNumber, response.accountNumber());
        assertEquals(BigDecimal.valueOf(5000), response.balance());
        verify(accountRepository).findByAccountNumber(accountNumber);
    }

	@Test
	@Order(8)
    void testBalanceAccountNotFound() {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        AccsException ex=  assertThrows(AccsException.class, () -> accountService.balance(accountNumber));
        assertEquals(ExceptionCode.ACC_ACCOUNT_NON_EXIST,ex.getExceptionCode());
        
        verify(accountRepository).findByAccountNumber(accountNumber);
    }

	@Test
	@Order(8)
    void testBalanceAccountInvalidStatus() {
		account.setAccountStatus(AccountStatus.CLOSED);
		
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccsException ex=  assertThrows(AccsException.class, () -> accountService.balance(accountNumber));
        assertEquals(ExceptionCode.ACC_ACCOUNT_STATUS_INVALID,ex.getExceptionCode());
        
        verify(accountRepository).findByAccountNumber(accountNumber);
    }
}
