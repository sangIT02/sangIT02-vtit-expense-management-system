package com.january.demo.controller.v1;


import com.january.demo.dto.BaseResponse;
import com.january.demo.dto.request.ChangePasswordRequest;
import com.january.demo.dto.request.LoginRequest;
import com.january.demo.dto.request.RefreshTokenRequest;
import com.january.demo.dto.request.RegisterRequest;
import com.january.demo.dto.response.LoginResponse;
import com.january.demo.dto.response.RegisterResponse;
import com.january.demo.repository.UserRepository;
import com.january.demo.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
@Tag(name = "Authentication", description = "Các API xác thực người dùng")
@RestController
@RequestMapping("${app.api-prefix}/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(
            summary = "Đăng ký tài khoản",
            description = "Tạo tài khoản người dùng mới."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Đăng ký thành công"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu không hợp lệ"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Tên đăng nhập hoặc email đã tồn tại"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        RegisterResponse response = userService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Đăng ký thành công", response));
    }


    @Operation(
            summary = "Đăng nhập tài khoản",
            description = "API dùng để đăng nhập vào tài khoản người dùng."
    )
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("Đăng nhập thành công", response));
    }

    @Operation(
            summary = "Làm mới token",
            description = "API dùng để cấp lại access token và refresh token từ refresh token hợp lệ."
    )
    @PostMapping("/refresh-token")
    public BaseResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return BaseResponse.success("Refresh token thành công",userService.refreshToken(refreshTokenRequest));
    }


    @Operation(
            summary = "Đổi mật khẩu",
            description = "API dùng để đổi mật khẩu cho người dùng đang đăng nhập."
    )
    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<String>> changePass(@Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("Đổi mật khẩu thành công"));
    }
}
