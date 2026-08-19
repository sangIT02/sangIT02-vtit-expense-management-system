package com.january.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResponse(
        Long id,
        String name,
        Long categoryId,
        BigDecimal amount,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        Integer usagePercentage
) {
}