package com.january.demo.dto.response;

import com.january.demo.enums.CategoryType;

import java.util.List;

public record CategoryTreeNode(
        Long id,
        String name,
        CategoryType type,
        List<CategoryTreeNode> children
) {
}