package com.structurax.root.structurax.root.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmployeePassword(String toEmail, String name, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Structurax - Your Account Credentials");
            helper.setText(
                    "Hi " + name + ",\n\n" +
                            "Your Structurax employee account has been created successfully.\n\n" +
                            "Here is your login password:\n" +
                            "**" + password + "**\n\n" +
                            "Please log in and change it as soon as possible.\n\n" +
                            "Regards,\n" +
                            "Structurax Administration"
            );

            mailSender.send(message);
            log.info("📧 Email sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
        }
    }
}
