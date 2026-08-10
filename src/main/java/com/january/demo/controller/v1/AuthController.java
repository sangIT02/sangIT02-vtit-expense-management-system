package com.january.demo.controller.v1;

import com.january.demo.dto.request.RefreshTokenRequest;
import com.january.demo.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-prefix}/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;

    @Operation(
            summary = "Đăng xuất",
            description = "API dùng để thu hồi refresh token của người dùng đang đăng nhập."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        userService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
