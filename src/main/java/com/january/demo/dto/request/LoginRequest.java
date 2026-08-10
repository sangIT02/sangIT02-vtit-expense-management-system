package com.january.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "tên đăng nhập không được để trống")
        String username,

        @NotBlank(message = "mật khẩu không được để trống")
                @Size(min = 8, message = "Mật khẩu phải từ 8 kí tự")
        String password
) {
}
