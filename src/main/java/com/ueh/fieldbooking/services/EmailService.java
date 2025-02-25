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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Async
    public void sendResetPasswordEmail(String email, String fullName, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(Map.of(
                    "fullName", fullName,
                    "token", token
            ));

            String html = templateEngine.process("reset-email", context);

            helper.addTo(email);
            helper.setSubject("🔐 Yêu cầu đặt lại mật khẩu");
            helper.setText(html, true);
            helper.setFrom(fromMail);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Lỗi khi gửi emai đặt lại mật khẩu đến {}", email, e);
        }
    }

    @Async
    public void sendCancelBookingEmail(String email, String name, Integer score, LocalDate date) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(Map.of(
                    "userName", name,
                    "refundPoints", score,
                    "bookingDate", date,
                    "websiteUrl", "https://fieldweb.onrender.com/"
            ));

            String html = templateEngine.process("refund-email", context);

            helper.addTo(email);
            helper.setSubject("💰 Hoàn Trả Điểm Hủy Đơn");
            helper.setText(html, true);
            helper.setFrom(fromMail);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Lỗi khi gửi email: ", e);
        }
    }
}
