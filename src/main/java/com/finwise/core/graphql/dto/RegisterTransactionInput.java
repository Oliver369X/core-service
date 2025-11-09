package com.finwise.core.graphql.dto;

import com.finwise.core.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RegisterTransactionInput(
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        String description,
        OffsetDateTime occurredAt
) {
}

