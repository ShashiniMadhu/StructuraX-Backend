package com.structurax.root.structurax.root.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
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
    public void sendEmployeeOtp(String toEmail, String name, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Structurax - Your Account Credentials");
            helper.setText(
                    "Hi " + name + ",\n\n" +
                            "Your Structurax employee account has been created successfully.\n\n" +
                            "Here is your one-time login password (OTP):\n" +
                            "**" + otp + "**\n\n" +
                            "Please log in and change it as soon as possible.\n\n" +
                            "Regards,\n" +
                            "Structurax Administration"
            );

            mailSender.send(message);
            log.info("📧 OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email: {}", e.getMessage());
        }
    }


    @Async
    public void sendRemovalNotification(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Account Deactivation Notice - Structurax");
            helper.setText(
                    "Dear " + name + ",\n\n" +
                            "We would like to inform you that your employment with Structurax has ended.\n\n" +
                            "Your account has been deactivated and you will no longer have access to the system.\n\n" +
                            "If you believe this is a mistake, please contact HR.\n\n" +
                            "Best regards,\n" +
                            "Structurax Administration"
            );

            mailSender.send(message);
            log.info("📧 Removal email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send removal email: {}", e.getMessage());
        }
    }

    @Async
    public void sendClientRegisterOtp(String toEmail, String name, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Structurax - Your Account Credentials");
            helper.setText(
                    "Hi " + name + ",\n\n" +
                            "Your Structurax Client account has been created successfully.\n\n" +
                            "Here is your login Otp :\n" +
                            "**" + otp + "**\n\n" +
                            "Please log in and change it as soon as possible.\n\n" +
                            "Regards,\n" +
                            "Structurax Director"
            );

            mailSender.send(message);
            log.info("📧 Email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
           log.error("failed to send otp: {}",e.getMessage());
        }
    }

    @Async
    public void sendSupplierOtp(String toEmail, String supplierName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to Structurax - Supplier Account Credentials");
            helper.setText(
                    "Dear " + supplierName + ",\n\n" +
                            "Your Structurax supplier account has been created successfully.\n\n" +
                            "Here is your login OTP (One-Time Password):\n" +
                            "**" + otp + "**\n\n" +
                            "Please use this OTP to access your supplier portal and update your account details.\n\n" +
                            "For security purposes, please change your password after your first login.\n\n" +
                            "If you have any questions or need assistance, please contact our procurement team.\n\n" +
                            "Best regards,\n" +
                            "Structurax Administration Team"
            );

            mailSender.send(message);
            log.info("📧 Supplier OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send supplier OTP email: {}", e.getMessage());
        }
    }

    public void sendPassswordResetEmail(String toEmail,String username, String resetLink){
        try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("StructuraX Password Reset Request");

                String emailBody = String.format(
                        "Dear %s, \n\n" +
                                "We received a request to reset your structuraX account password.\n\n" +
                                "Please click the link below to reset your password (valid for 15 minutes):\n" +
                                "%s\n\n" +
                                "If you did not request this, you can safely ignore this email.\n\n" +
                                "Best Regards,\n" +
                                "StructuraX Administration Team",
                        username,resetLink

                );

                message.setText(emailBody);
                mailSender.send(message);
                System.out.println("Password reset email sent successfully to "+ toEmail);
            }catch(Exception e){
                System.err.println("Failed to send a password rest email to: "+ toEmail + ", Error: "+ e.getMessage());
                throw new RuntimeException("Failed to send password reset email: " + e.getMessage());

            }

        }
}
