package com.january.demo.security;

import com.january.demo.dto.projection.PermissionNameProjection;
import com.january.demo.entity.*;
import com.january.demo.repository.PermissionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    /**
     * Lay danh sach quyen cua nguoi dung dang dang nhap de Spring Security
     * su dung trong cac cau hinh phan quyen nhu {@code hasRole} va
     * {@code hasAuthority}. Role se duoc them tien to {@code ROLE_}, con
     * permission duoc tra ve theo dung ten enum trong bang permission.
     *
     * @return danh sach {@link GrantedAuthority} gom role va permission cua user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.emptyList();
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull) // Ngăn lỗi NullPointerException
                .forEach(role -> {
                    // 1. Thêm Role (có tiền tố ROLE_)
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                    // 2. Thêm tất cả Permissions của Role đó (không có tiền tố)
                    if (role.getRolePermissions() != null) {
                        role.getRolePermissions().stream()
                                .map(RolePermission::getPermission)
                                .filter(Objects::nonNull)
                                .map(Permission::getName)
                                .forEach(permName -> authorities.add(new SimpleGrantedAuthority(permName.name())));
                    }
                });
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
