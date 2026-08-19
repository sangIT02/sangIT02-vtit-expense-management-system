package com.january.demo.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotNull(message = "Ten muc tieu khong duoc de trong")
        @Size(max = 100, message = "Ten muc tieu khong duoc vuot qua 100 ky tu")
        String name,

        @NotNull(message = "So tien muc tieu khong duoc de trong")
        @Positive(message = "So tien muc tieu phai lon hon 0")
        @Digits(integer = 17, fraction = 2, message = "So tien muc tieu khong hop le")
        BigDecimal targetAmount,

        @PositiveOrZero(message = "So tien hien tai phai lon hon hoac bang 0")
        @Digits(integer = 17, fraction = 2, message = "So tien hien tai khong hop le")
        BigDecimal currentAmount,

        LocalDate deadline,

        @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
        String description
) {
}