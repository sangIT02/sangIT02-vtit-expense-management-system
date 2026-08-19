package com.january.demo.dto.response;

import com.january.demo.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        TransactionType type,
        Long walletId,
        Long categoryId,
        BigDecimal amount,
        LocalDateTime transactionDate,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}