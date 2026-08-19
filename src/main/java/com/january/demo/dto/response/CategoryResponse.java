package com.january.demo.dto.response;

import com.january.demo.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}