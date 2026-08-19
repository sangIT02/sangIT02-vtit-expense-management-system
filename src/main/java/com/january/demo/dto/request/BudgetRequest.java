package com.january.demo.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequest(
        @NotNull(message = "Ten ngan sach khong duoc de trong")
        @Size(max = 100, message = "Ten ngan sach khong duoc vuot qua 100 ky tu")
        String name,

        Long categoryId,

        @NotNull(message = "So tien ngan sach khong duoc de trong")
        @Positive(message = "So tien ngan sach phai lon hon 0")
        @Digits(integer = 17, fraction = 2, message = "So tien ngan sach khong hop le")
        BigDecimal amount,

        @NotNull(message = "Ngay bat dau khong duoc de trong")
        LocalDate startDate,

        @NotNull(message = "Ngay ket thuc khong duoc de trong")
        LocalDate endDate
) {
}