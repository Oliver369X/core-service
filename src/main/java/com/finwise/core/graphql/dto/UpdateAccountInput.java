package com.finwise.core.graphql.dto;

import com.finwise.core.domain.model.AccountType;

public record UpdateAccountInput(
        Long id,
        String name,
        AccountType type,
        String currency
) {
}




