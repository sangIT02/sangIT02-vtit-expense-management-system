package com.january.demo.controller.v1;

import com.january.demo.dto.BaseResponse;
import com.january.demo.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/v1/auth")
@RequiredArgsConstructor
public class AuthorityTestController {

    @Operation(
            summary = "Test authorities cua user dang dang nhap",
            description = "API dung de kiem tra danh sach authorities sau khi dang nhap va gui access token."
    )
    @GetMapping("/authorities")
    public ResponseEntity<BaseResponse<List<String>>> getAuthorities(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Vui long dang nhap de xem authorities");
        }

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(BaseResponse.success("Lay authorities thanh cong", authorities));
    }

    @Operation(
            summary = "Test hasAuthority USER_READ",
            description = "API dung de test cau hinh hasAuthority trong SecurityFilterChain."
    )
    @GetMapping("/authority/user-read")
    public ResponseEntity<BaseResponse<String>> testUserReadAuthority() {
        return ResponseEntity.ok(BaseResponse.success("Ban co authority USER_READ"));
    }
}
