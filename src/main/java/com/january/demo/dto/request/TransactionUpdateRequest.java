package com.january.demo.dto.request;

import com.january.demo.enums.TransactionType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateRequest(
        @NotNull(message = "Loai giao dich khong duoc de trong")
        TransactionType type,

        @NotNull(message = "Vi khong duoc de trong")
        Long walletId,

        @NotNull(message = "Danh muc khong duoc de trong")
        Long categoryId,

        @NotNull(message = "So tien khong duoc de trong")
        @Positive(message = "So tien phai lon hon 0")
        @Digits(integer = 17, fraction = 2, message = "So tien khong hop le")
        BigDecimal amount,

        @NotNull(message = "Ngay giao dich khong duoc de trong")
        LocalDateTime transactionDate,

        @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
        String description
) {
}