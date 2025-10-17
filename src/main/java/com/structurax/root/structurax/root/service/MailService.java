package com.structurax.root.structurax.root.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

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
            log.error("❌ Failed to send OTP email: {}", e.getMessage());
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
            log.error("❌ Failed to send removal email: {}", e.getMessage());
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
            log.error("❌ Failed to send supplier OTP email: {}", e.getMessage());
        }
    }

    @Async
    public void sendQuotationRequest(String toEmail, String supplierName, Integer quotationId, String projectName, String qsName, String qsEmail, String deadline) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Quotation Request - Quotation #" + quotationId + " - " + projectName);
            helper.setText(
                    "Dear " + supplierName + ",\n\n" +
                            "StructuraX is pleased to invite you to submit a quotation for the following project:\n\n" +
                            "📋 PROJECT DETAILS:\n" +
                            "• Project: " + projectName + "\n" +
                            "• Quotation Number: " + quotationId + "\n" +
                            "• Submission Deadline: " + deadline + "\n\n" +
                            "👨‍💼 CONTACT INFORMATION:\n" +
                            "• Quantity Surveyor: " + qsName + "\n" +
                            "• QS Email: " + qsEmail + "\n\n" +
                            "📝 NEXT STEPS:\n" +
                            "1. Please log in to your StructuraX supplier portal to view the complete quotation details\n" +
                            "2. Review all project specifications and requirements\n" +
                            "3. Submit your competitive quotation before the deadline\n" +
                            "4. Contact our QS if you have any technical questions\n\n" +
                            "We look forward to receiving your quotation and thank you for your continued partnership with StructuraX.\n\n" +
                            "Best regards,\n" +
                            "StructuraX Procurement Team\n\n" +
                            "---\n" +
                            "This is an automated message. Please do not reply to this email.\n" +
                            "For support, contact: " + qsEmail
            );

            mailSender.send(message);
            log.info("📧 Quotation request email sent to {} for quotation #{}", toEmail, quotationId);
        } catch (MessagingException e) {
            log.error("❌ Failed to send quotation request email: {}", e.getMessage());
        }
    }

    @Async
    public void sendPurchaseOrderNotification(String toEmail, String supplierName, Integer orderId, String projectName, String qsName, String qsEmail, String orderDate, String deliveryDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Purchase Order - Order #" + orderId + " - " + projectName);
            helper.setText(
                    "Dear " + supplierName + ",\n\n" +
                            "Congratulations! StructuraX is pleased to inform you that your quotation has been accepted.\n\n" +
                            "📦 PURCHASE ORDER DETAILS:\n" +
                            "• Purchase Order Number: " + orderId + "\n" +
                            "• Project: " + projectName + "\n" +
                            "• Order Date: " + orderDate + "\n" +
                            "• Expected Delivery Date: " + (deliveryDate != null ? deliveryDate : "To be confirmed") + "\n\n" +
                            "👨‍💼 PROJECT CONTACT:\n" +
                            "• Quantity Surveyor: " + qsName + "\n" +
                            "• QS Email: " + qsEmail + "\n\n" +
                            "📋 ACTION REQUIRED:\n" +
                            "1. Please log in to your StructuraX supplier portal to view the complete purchase order\n" +
                            "2. Review all order details and specifications\n" +
                            "3. Confirm order acceptance and delivery schedule\n" +
                            "4. Begin procurement/production as per the agreed timeline\n" +
                            "5. Update delivery status regularly through the portal\n\n" +
                            "📞 SUPPORT:\n" +
                            "If you have any questions about this purchase order, please contact our QS directly.\n\n" +
                            "We appreciate your partnership and look forward to successful project completion.\n\n" +
                            "Best regards,\n" +
                            "StructuraX Procurement Team\n\n" +
                            "---\n" +
                            "This is an automated message. Please do not reply to this email.\n" +
                            "For support, contact: " + qsEmail
            );

            mailSender.send(message);
            log.info("📧 Purchase order notification sent to {} for order #{}", toEmail, orderId);
        } catch (MessagingException e) {
            log.error("❌ Failed to send purchase order notification: {}", e.getMessage());
        }
    }

}
