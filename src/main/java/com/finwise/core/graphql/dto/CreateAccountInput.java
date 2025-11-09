package com.finwise.core.graphql.dto;

import com.finwise.core.domain.model.AccountType;
import java.math.BigDecimal;

public record CreateAccountInput(
        String userId,
        String name,
        AccountType type,
        BigDecimal balance,
        String currency
) {
}




