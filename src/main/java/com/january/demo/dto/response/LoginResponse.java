package com.january.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
