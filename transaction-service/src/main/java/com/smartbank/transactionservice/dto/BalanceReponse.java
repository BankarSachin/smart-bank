package com.smartbank.transactionservice.dto;

import java.math.BigDecimal;

public record BalanceReponse(String accountNumber,BigDecimal balance) {

}
