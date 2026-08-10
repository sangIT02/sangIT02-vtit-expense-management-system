package com.january.demo.service;

import com.january.demo.dto.request.ChangePasswordRequest;
import com.january.demo.dto.request.LoginRequest;
import com.january.demo.dto.request.RefreshTokenRequest;
import com.january.demo.dto.request.RegisterRequest;
import com.january.demo.dto.response.LoginResponse;
import com.january.demo.dto.response.RegisterResponse;

import java.text.ParseException;

public interface IUserService {

    /**
     * Đăng ký tài khoản người dùng mới từ thông tin được gửi lên.
     *
     * <p>Phương thức nhận dữ liệu đăng ký, xử lý tạo người dùng mới và trả về
     * thông tin người dùng sau khi đăng ký thành công.</p>
     *
     * @param registerRequest thông tin đăng ký của người dùng, bao gồm tên đăng nhập,
     *                        email, mật khẩu, họ tên và số điện thoại
     * @return thông tin người dùng đã được đăng ký
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * Đăng nhập người dùng bằng tên đăng nhập và mật khẩu.
     *
     * <p>Phương thức kiểm tra thông tin đăng nhập, trạng thái tài khoản và tạo access token,
     * refresh token khi đăng nhập thành công.</p>
     *
     * @param loginRequest thông tin đăng nhập gồm tên đăng nhập và mật khẩu
     * @return thông tin token sau khi đăng nhập thành công
     * @throws ParseException nếu refresh token không thể phân tích để lấy thông tin hết hạn
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Làm mới token đăng nhập từ refresh token hiện có.
     *
     * <p>Phương thức kiểm tra refresh token có tồn tại, còn hiệu lực, chưa bị thu hồi và hợp lệ
     * trước khi cấp token mới.</p>
     *
     * @param request thông tin refresh token cần dùng để cấp lại token
     * @return thông tin access token và refresh token mới
     */
    LoginResponse refreshToken(RefreshTokenRequest request);

    /**
     * Đăng xuất người dùng bằng cách thu hồi refresh token.
     *
     * <p>Phương thức tìm refresh token trong cơ sở dữ liệu, kiểm tra token chưa bị thu hồi và
     * đánh dấu {@code revoked = true} để token không thể được sử dụng lại.</p>
     *
     * @param request thông tin refresh token cần thu hồi
     */
    void logout(RefreshTokenRequest request);

    /**
     * Đổi mật khẩu cho người dùng đang đăng nhập.
     *
     * <p>Phương thức kiểm tra mật khẩu mới khác mật khẩu cũ, lấy người dùng hiện tại từ ngữ cảnh
     * bảo mật và cập nhật mật khẩu đã được mã hóa.</p>
     *
     * @param request thông tin mật khẩu cũ và mật khẩu mới
     */
    void changePassword(ChangePasswordRequest request);
}
