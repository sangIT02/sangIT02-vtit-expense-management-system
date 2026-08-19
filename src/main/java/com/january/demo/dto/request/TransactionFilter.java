package com.january.demo.dto.request;

import com.january.demo.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionFilter(
        TransactionType type,
        Long walletId,
        Long categoryId,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        java.math.BigDecimal minAmount,
        java.math.BigDecimal maxAmount,
        String keyword
) {
}