package com.january.demo.dto.response;

import com.january.demo.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponse(
        Long id,
        String name,
        String currency,
        BigDecimal balance,
        WalletStatus status,
        String description,
        BigDecimal totalIn,
        BigDecimal totalOut,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}