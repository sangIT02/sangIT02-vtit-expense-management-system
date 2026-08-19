package com.january.demo.dto.response;

import java.math.BigDecimal;

public record ReconciliationResponse(
        Long walletId,
        String walletName,
        BigDecimal initialBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal totalTransferIn,
        BigDecimal totalTransferOut,
        BigDecimal calculatedBalance,
        BigDecimal actualBalance,
        BigDecimal difference,
        String status
) {
}