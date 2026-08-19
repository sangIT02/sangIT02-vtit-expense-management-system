package com.january.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
        @NotBlank(message = "Ten danh muc khong duoc de trong")
        @Size(max = 100, message = "Ten danh muc khong duoc vuot qua 100 ky tu")
        String name,

        Long parentId
) {
}