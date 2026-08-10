package com.january.demo.entity;

import com.january.demo.constant.RegexConstants;
import com.january.demo.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true,length = 50)
    @NotBlank(message = "User name không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải có từ 3 đến 50 ký tự")
    private String username;

    @Column(name = "email", nullable = false, unique = true,length = 100)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, max = 100, message = "password phải từ 8 đến 255 ký tự")
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;

    @Column(name = "phone", length = 50)
    @Pattern(regexp = RegexConstants.PHONE_REGEX, message = "Số điện thoại không hợp lệ")
    private String phone;

    @Column(name = "avatar_url",length = 50)
    @Size(max = 255)
    private String avatarUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @OneToMany(
            mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true
    )
    private Set<UserRole> roles = new HashSet<>();


    @OneToMany(
            mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY
    )
    private Set<RefreshToken> refreshTokens = new HashSet<>();}
