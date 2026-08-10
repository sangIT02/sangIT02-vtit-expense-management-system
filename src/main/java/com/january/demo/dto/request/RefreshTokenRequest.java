package com.january.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "refresh token không được trống")
        String refreshToken

) {
}
