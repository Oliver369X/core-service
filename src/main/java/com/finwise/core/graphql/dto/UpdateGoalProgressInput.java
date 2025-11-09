package com.finwise.core.graphql.dto;

import java.math.BigDecimal;

public record UpdateGoalProgressInput(
        Long id,
        BigDecimal currentAmount
) {
}

