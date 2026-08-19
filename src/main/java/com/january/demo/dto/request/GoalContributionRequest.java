package com.january.demo.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record GoalContributionRequest(
        @NotNull(message = "So tien gop khong duoc de trong")
        @Positive(message = "So tien gop phai lon hon 0")
        @Digits(integer = 17, fraction = 2, message = "So tien gop khong hop le")
        BigDecimal amount
) {
}