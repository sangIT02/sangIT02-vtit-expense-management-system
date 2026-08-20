package com.january.demo.service;

public interface IMailService {

    /**
     * Gửi email chứa mã OTP đến địa chỉ email chỉ định.
     *
     * <p>Email được gửi có chủ đề xác nhận khôi phục mật khẩu, nội dung gồm mã OTP
     * và thời gian hiệu lực của mã.</p>
     *
     * @param toEmail địa chỉ email người nhận
     * @param otp     mã OTP cần gửi đến người dùng
     */
    void sendOptEmail(String toEmail, String otp);
}