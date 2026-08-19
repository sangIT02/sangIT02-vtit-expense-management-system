package com.january.demo.dto.response;

import com.january.demo.enums.PermissionName;

import java.time.LocalDateTime;

public record ApiPermissionResponse(
        Long id,
        String httpMethod,
        String urlPattern,
        PermissionName permissionName,
        String description,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
