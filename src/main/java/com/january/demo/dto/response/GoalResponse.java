package com.january.demo.dto.response;

import com.january.demo.enums.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        Integer progressPercentage,
        LocalDate deadline,
        GoalStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}