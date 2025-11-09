package com.finwise.core.graphql.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBudgetInput(
        Long id,
        BigDecimal limitAmount,
        LocalDate periodStart,
        LocalDate periodEnd
) {
}




