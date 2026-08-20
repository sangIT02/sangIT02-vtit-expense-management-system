package com.january.demo.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryRequest(
        @NotBlank(message = "Transaction ID không được để trống")
        @Size(max = 100, message = "Transaction ID không được vượt quá 100 ký tự")
        String transactionId,

        @NotBlank(message = "Tài khoản nguồn không được để trống")
        @Size(max = 50, message = "Tài khoản nguồn không được vượt quá 50 ký tự")
        String sourceAccount,

        @NotBlank(message = "Tài khoản đích không được để trống")
        @Size(max = 50, message = "Tài khoản đích không được vượt quá 50 ký tự")
        String destinationAccount,

        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
        BigDecimal amount

) {
}
