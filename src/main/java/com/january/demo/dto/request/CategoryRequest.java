package com.january.demo.dto.request;

import com.january.demo.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Ten danh muc khong duoc de trong")
        @Size(max = 100, message = "Ten danh muc khong duoc vuot qua 100 ky tu")
        String name,

        @NotNull(message = "Loai danh muc khong duoc de trong")
        CategoryType type,

        Long parentId
) {
}