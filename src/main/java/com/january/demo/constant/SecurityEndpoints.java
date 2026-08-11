package com.january.demo.constant;


import org.springframework.beans.factory.annotation.Value;

public final class SecurityEndpoints {
    private SecurityEndpoints() {} // Ngăn khởi tạo instance

    public static final String API_PREFIX = "/api";

    public static final String[] USER_ENDPOINTS = {
            API_PREFIX + "/v1/users/**",
            API_PREFIX + "/v1/users/change-password"
    };

    public static final String[] ADMIN_ENDPOINTS = {
            API_PREFIX + "/v1/admin/**"
    };

    public static final String[] PUBLIC_ENDPOINTS = {
//            API_PREFIX + "/v1/auth/**",
            API_PREFIX + "/v1/users/login",
            API_PREFIX + "/v1/users/register",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public static final String[] USER_POST_ENDPOINTS = {
            API_PREFIX + "/v1/auth/logout"
    };

    public static final String[] USER_PUT_ENDPOINTS = {
            API_PREFIX + "/v1/users/change-password"
    };

    // Nhóm Endpoint áp dụng cho Authority USER_READ
    public static final String[] USER_READ_AUTHORITY_GET_ENDPOINTS = {
            API_PREFIX + "/v1/auth/authority/user-read",
    };
}

