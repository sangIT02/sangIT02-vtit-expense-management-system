package com.january.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletUpdateRequest(
        @NotBlank(message = "Ten vi khong duoc de trong")
        @Size(max = 100, message = "Ten vi khong duoc vuot qua 100 ky tu")
        String name,

        @Size(max = 10, message = "Don vi tien te khong duoc vuot qua 10 ky tu")
        String currency,

        @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
        String description
) {
}