package com.structurax.root.structurax.root.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

    @Async
public void sendQuotationRequest(String toEmail, String supplierName, Integer quotationId, String projectName, String qsName, String qsEmail, String deadline) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Invitation to Quote for " + projectName + " (Ref: #" + quotationId + ")");

        // --- Professional HTML Email Body with StructuraX Branding ---
        String htmlBody = "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head><meta charset='UTF-8'><title>Quotation Request</title></head>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; padding: 20px;'>"
                + "  <div style='max-width: 680px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>"
                + "    <div style='text-align: center; border-bottom: 2px solid #eee; padding-bottom: 20px; margin-bottom: 25px;'>"
                // Header mimicking your logo style
                + "      <h1 style='color: #222222; margin: 0; font-size: 32px; font-weight: bold;'>Structura<span style='color: #FDBA12;'>X</span></h1>"
                + "      <h2 style='color: #333; margin: 5px 0 0 0; font-weight: 300;'>Invitation to Quote</h2>"
                + "    </div>"
                + "    <p style='font-size: 16px;'>Dear " + supplierName + ",</p>"
                + "    <p>StructuraX is pleased to invite your company to submit a formal quotation for the project detailed below. We value your expertise and look forward to potentially collaborating on this venture.</p>"
                + "    <div style='background-color: #fdfdfd; padding: 20px; border-left: 5px solid #FDBA12; margin: 25px 0; border-radius: 5px;'>"
                + "      <h3 style='margin-top: 0; color: #222222;'>Project & Quotation Details</h3>"
                + "      <p style='margin: 5px 0;'><strong>Project Name:</strong> " + projectName + "</p>"
                + "      <p style='margin: 5px 0;'><strong>Quotation Reference #:</strong> " + quotationId + "</p>"
                + "      <p style='margin: 5px 0;'><strong>Submission Deadline:</strong> <span style='color: #d9534f; font-weight: bold;'>" + deadline + "</span></p>"
                + "    </div>"
                // Call-to-action button styled like your "LOGIN" button
                + "    <div style='text-align: center; margin: 35px 0;'>"
                + "      <a href='https://your-portal-url.com/quotations/" + quotationId + "' style='background-color: #FDBA12; color: #000000; padding: 14px 28px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold; display: inline-block;'>View Details & Submit Quotation</a>"
                + "    </div>"
                + "    <p>To access the complete project specifications and submit your quotation, please click the button above to visit your secure supplier portal.</p>"
                + "    <p>Should you have any questions regarding the project specifications, please do not hesitate to contact our assigned Quantity Surveyor:</p>"
                + "    <p style='margin-left: 20px;'><strong>Name:</strong> " + qsName + "<br>"
                + "    <strong>Email:</strong> <a href='mailto:" + qsEmail + "' style='color: #FDBA12; text-decoration: none;'>" + qsEmail + "</a></p>"
                + "    <p style='margin-top: 30px;'>Thank you for your time and consideration. We appreciate your partnership and look forward to receiving your proposal.</p>"
                + "    <p style='margin-top: 25px;'>Best regards,</p>"
                + "    <p style='margin-bottom: 0;'><strong>The Procurement Team</strong><br>StructuraX</p>"
                + "    <hr style='border: none; border-top: 1px solid #eee; margin: 25px 0;'>"
                + "    <p style='font-size: 12px; color: #888; text-align: center;'>This is an automated notification from the StructuraX system. Please do not reply directly to this email.</p>"
                + "  </div>"
                + "</body>"
                + "</html>";

        helper.setText(htmlBody, true);

        mailSender.send(message);
        log.info("📧 Branded quotation request email sent to {} for quotation #{}", toEmail, quotationId);
    } catch (MessagingException e) {
        log.error("❌ Failed to send branded quotation request email: {}", e.getMessage());
    }
}

   @Async
public void sendPurchaseOrderNotification(String toEmail, String supplierName, Integer orderId, String projectName, String qsName, String qsEmail, String orderDate, String deliveryDate) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Purchase Order Confirmation #" + orderId + " for " + projectName);

        // --- Professional HTML Email Body with StructuraX Branding ---
        String htmlBody = "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head><meta charset='UTF-8'><title>Purchase Order Confirmation</title></head>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; padding: 20px;'>"
                + "  <div style='max-width: 680px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>"
                + "    <div style='text-align: center; border-bottom: 2px solid #eee; padding-bottom: 20px; margin-bottom: 25px;'>"
                + "      <h1 style='color: #222222; margin: 0; font-size: 32px; font-weight: bold;'>Structura<span style='color: #FDBA12;'>X</span></h1>"
                + "      <h2 style='color: #333; margin: 5px 0 0 0; font-weight: 300;'>Purchase Order Confirmation</h2>"
                + "    </div>"
                + "    <p style='font-size: 16px;'>Dear " + supplierName + ",</p>"
                + "    <p>Congratulations! We are pleased to inform you that your quotation for the <strong>" + projectName + "</strong> project has been accepted. Please find the official purchase order details below.</p>"
                + "    <div style='background-color: #fdfdfd; padding: 20px; border-left: 5px solid #FDBA12; margin: 25px 0; border-radius: 5px;'>"
                + "      <h3 style='margin-top: 0; color: #222222;'>Purchase Order Details</h3>"
                + "      <p style='margin: 5px 0;'><strong>PO Reference #:</strong> " + orderId + "</p>"
                + "      <p style='margin: 5px 0;'><strong>Project Name:</strong> " + projectName + "</p>"
                + "      <p style='margin: 5px 0;'><strong>Order Date:</strong> " + orderDate + "</p>"
                + "      <p style='margin: 5px 0;'><strong>Expected Delivery Date:</strong> " + (deliveryDate != null ? deliveryDate : "To be confirmed") + "</p>"
                + "    </div>"
                + "    <h3 style='color: #222222; border-bottom: 2px solid #eee; padding-bottom: 5px;'>Action Required</h3>"
                + "    <p>Please log in to the StructuraX Supplier Portal to formally accept this purchase order and confirm the delivery schedule.</p>"
                + "    <div style='text-align: center; margin: 35px 0;'>"
                // IMPORTANT: Replace with your actual portal URL
                + "      <a href='https://your-portal-url.com/orders/" + orderId + "' style='background-color: #FDBA12; color: #000000; padding: 14px 28px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold; display: inline-block;'>View Purchase Order & Confirm</a>"
                + "    </div>"
                + "    <p>If you have any questions regarding this order, please contact the assigned Quantity Surveyor:</p>"
                + "    <p style='margin-left: 20px;'><strong>Name:</strong> " + qsName + "<br>"
                + "    <strong>Email:</strong> <a href='mailto:" + qsEmail + "' style='color: #FDBA12; text-decoration: none;'>" + qsEmail + "</a></p>"
                + "    <p style='margin-top: 30px;'>We look forward to a successful collaboration on this project.</p>"
                + "    <p style='margin-top: 25px;'>Best regards,</p>"
                + "    <p style='margin-bottom: 0;'><strong>The Procurement Team</strong><br>StructuraX</p>"
                + "    <hr style='border: none; border-top: 1px solid #eee; margin: 25px 0;'>"
                + "    <p style='font-size: 12px; color: #888; text-align: center;'>This is an automated notification from the StructuraX system. Please do not reply directly to this email.</p>"
                + "  </div>"
                + "</body>"
                + "</html>";

        helper.setText(htmlBody, true);

        mailSender.send(message);
        log.info("📧 Branded purchase order notification sent to {} for order #{}", toEmail, orderId);
    } catch (MessagingException e) {
        log.error("❌ Failed to send branded purchase order notification: {}", e.getMessage());
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