package com.january.demo.utils;

import com.january.demo.security.CustomUserDetails;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Cung cấp các tiện ích đọc thông tin người dùng hiện tại từ Spring Security context.
 */
@NoArgsConstructor
public final class SecurityUtils {

    /**
     * Lấy đối tượng xác thực hiện tại từ {@link SecurityContextHolder}.
     *
     * @return thông tin xác thực hiện tại
     * @throws IllegalStateException nếu request hiện tại chưa được xác thực
     */
    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated");
        }

        return authentication;
    }

    /**
     * Lấy thông tin chi tiết của người dùng đang đăng nhập.
     *
     * @return thông tin người dùng hiện tại
     * @throws IllegalStateException nếu principal không phải {@link CustomUserDetails}
     */
    public static CustomUserDetails getCurrentUser() {
        Object principal = getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        return userDetails;
    }

    /**
     * Lấy id của người dùng đang đăng nhập.
     *
     * @return id của người dùng hiện tại
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUser().getId();
    }

    /**
     * Lấy username của người dùng đang đăng nhập.
     *
     * @return username của người dùng hiện tại
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}
