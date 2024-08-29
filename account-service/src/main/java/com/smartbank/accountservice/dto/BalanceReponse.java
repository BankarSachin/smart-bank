package com.smartbank.accountservice.dto;

import java.math.BigDecimal;

public record BalanceReponse(String accountNumber,BigDecimal balance) {

}
