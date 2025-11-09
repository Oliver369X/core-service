package com.finwise.core.graphql.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetInput(
        String userId,
        String category,
        BigDecimal limitAmount,
        LocalDate periodStart,
        LocalDate periodEnd
) {
}

