package com.january.demo.dto.request;

public record EncryptedTransactionHistoryRequest(
        String transactionId,
        String account,
        String inDebt,
        String have,
        String time
) {
}
