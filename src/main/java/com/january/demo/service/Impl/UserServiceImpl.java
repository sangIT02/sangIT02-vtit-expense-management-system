package com.january.demo.service.Impl;

import com.january.demo.dto.request.ChangePasswordRequest;
import com.january.demo.dto.request.LoginRequest;
import com.january.demo.dto.request.RefreshTokenRequest;
import com.january.demo.dto.request.RegisterRequest;
import com.january.demo.dto.response.LoginResponse;
import com.january.demo.dto.response.RegisterResponse;
import com.january.demo.entity.RefreshToken;
import com.january.demo.entity.Role;
import com.january.demo.entity.User;
import com.january.demo.entity.UserRole;
import com.january.demo.enums.RoleName;
import com.january.demo.enums.UserStatus;
import com.january.demo.exception.*;
import com.january.demo.repository.RefreshTokenRepository;
import com.january.demo.repository.RoleRepository;
import com.january.demo.repository.UserRepository;
import com.january.demo.repository.UserRoleRepository;
import com.january.demo.security.CustomUserDetails;
import com.january.demo.service.IUserService;
import com.january.demo.utils.JwtUtils;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())){
            throw new ConflictException("Tên đăng nhập đã tồn tại");
        }
        if(userRepository.existsByEmail(registerRequest.email())){
            throw new ConflictException("Email đã tồn tại");
        }
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role USER"));

        User user = User.builder()
                .username(registerRequest.username())
                .phone(registerRequest.phone())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .avatarUrl(registerRequest.avatarUrl())
                .fullName(registerRequest.fullName())
                .emailVerified(false)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        userRoleRepository.save(userRole);
        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new UnauthorizedException("Ten dang nhap hoac mat khau khong dung"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UnauthorizedException("Ten dang nhap hoac mat khau khong dung");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Tai khoan khong duoc phep dang nhap");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        Date expiration = jwtUtils.getExpirationFromToken(refreshToken);
        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .expiredAt(expiration)
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(token);
        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findRefreshTokenByToken(request.refreshToken());
        if (token == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        if(token.isRevoked()){
            throw new UnauthorizedException("Token revoked");
        }
        if(token.getExpiredAt().before(new Date())){
            throw new UnauthorizedException("Token is expired");
        }
        if(!jwtUtils.isRefrechTokenValid(request.refreshToken())){
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = token.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        Date expiration = jwtUtils.getExpirationFromToken(refreshToken);
        RefreshToken newToken = RefreshToken.builder()
                .token(refreshToken)
                .expiredAt(expiration)
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(newToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findRefreshTokenByToken(request.refreshToken());
        if (token == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        if (token.isRevoked()) {
            throw new UnauthorizedException("Token revoked");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        if (request.oldPassword().equals(request.newPassword())) {
            throw new BadRequestException("New password must be different from old password");
        }
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UnauthorizedException("User not found");
        });
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user = userRepository.save(user);
    }

}
