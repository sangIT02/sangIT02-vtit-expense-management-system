package com.january.demo.service.Impl;

import com.january.demo.service.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImlp implements IMailService {
    private final JavaMailSender mailSender;


    @Override
    public void sendOptEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("email.he.thong.cua.ban@gmail.com");
        message.setTo(toEmail);
        message.setSubject("[Tên App] Mã xác nhận khôi phục mật khẩu");
        message.setText("Chào bạn,\n\n" +
                "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n" +
                "Mã này sẽ hết hạn sau 5 phút. Tuyệt đối không chia sẻ mã này với bất kỳ ai.\n\n" +
                "Trân trọng,\nĐội ngũ hỗ trợ.");

        mailSender.send(message);
    }
}
