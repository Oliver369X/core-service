package com.finwise.core.graphql.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalInput(
        String userId,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate
) {
}

