package com.ueh.fieldbooking.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Async
    public void sendResetPasswordEmail(String email, String fullName, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.addTo(email);
            helper.setSubject("🔐 Yêu cầu đặt lại mật khẩu");
            helper.setText(createResetPasswordEmail(fullName, token), true);
            helper.setFrom(fromMail);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Lỗi khi gửi emai đặt lại mật khẩu đến {}", email, e);
        }
    }

    private String createResetPasswordEmail(String fullName, String token) {
        return "<html><body style='font-family: Arial, sans-serif; background-color: #f7f7f7; padding: 20px;'>"
                + "<div style='background-color: #ffffff; border-radius: 5px; padding: 20px; max-width: 600px; margin: 0 auto; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>"
                + "<h2 style='color: #2c3e50; text-align: center;'>🔐 Yêu cầu đặt lại mật khẩu</h2>"
                + "<p>Kính gửi " + fullName + ",</p>"
                + "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của Quý khách. Để tiếp tục quá trình này, vui lòng nhấp vào nút bên dưới:</p>"
                + "<div style='text-align: center;'>"
                + "<a href='http://localhost:3000/reset-password?token=" + token + "' "
                + "style='background-color: #3498db; border: none; color: white; padding: 15px 32px; "
                + "text-align: center; text-decoration: none; display: inline-block; font-size: 16px; "
                + "margin: 4px 2px; cursor: pointer; border-radius: 5px;'>"
                + "🔑 Đặt lại mật khẩu</a>"
                + "</div>"
                + "<p>⚠️ Lưu ý: Liên kết này sẽ hết hạn sau 24 giờ vì lý do bảo mật.</p>"
                + "<p>Nếu Quý khách không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ của chúng tôi nếu có bất kỳ thắc mắc nào.</p>"
                + "<p>Trân trọng,<br>Đội ngũ Bảo mật 🛡️</p>"
                + "</div>"
                + "</body></html>";
    }
}
