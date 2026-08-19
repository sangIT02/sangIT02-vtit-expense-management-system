package com.january.demo.dto.request;

import com.january.demo.enums.PermissionName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ApiPermissionRequest(
        @NotBlank(message = "HTTP method khong duoc de trong")
        @Pattern(regexp = "^(GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD|\\*)$",
                message = "HTTP method khong hop le, chi ho tro GET/POST/PUT/DELETE/PATCH/OPTIONS/HEAD/*")
        String httpMethod,

        @NotBlank(message = "URL pattern khong duoc de trong")
        @Size(max = 255, message = "URL pattern khong duoc vuot qua 255 ky tu")
        String urlPattern,

        @NotNull(message = "Quyen han khong duoc de trong")
        PermissionName permissionName,

        @Size(max = 255, message = "Mo ta khong duoc vuot qua 255 ky tu")
        String description,

        Boolean enabled
) {
}
