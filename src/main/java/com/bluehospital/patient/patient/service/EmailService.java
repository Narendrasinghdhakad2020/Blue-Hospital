package com.bluehospital.patient.patient.service;

import com.bluehospital.patient.patient.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // Method to send email for verification of email
    public void sendVerificationEmail(String to, String code) {
//        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject("Email Verification");
            mail.setText("Your OTP is: " + code);

            // Attempt to send the email
            javaMailSender.send(mail);

            // Log successful email sending
            logger.info("Email-Service: Email successfully sent to {}", to);
//            ApiResponse<SimpleMailMessage> response = new ApiResponse<>(
//                    HttpStatus.NO_CONTENT.value(),
//                    "Email Successfully Sended to provided email",
//                    "/api/v1/public/patient/login",
//                    mail
//            );
//            return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
//        } catch (MailSendException ex) {
//            // Handles errors specific to sending email (e.g., invalid address format)
//            logger.error("Email-Service: Failed to send email to {} due to sending issue: {}", to, ex.getMessage());
//        } catch (MailException ex) {
//            // Handles generic mail-related exceptions
//            logger.error("Email-Service: General mail sending error for {}: {}", to, ex.getMessage());
//        } catch (IllegalArgumentException ex) {
//            // Handles cases like null or invalid input arguments
//            logger.error("Email-Service: Invalid input provided for {}: {}", to, ex.getMessage());
//        } catch (Exception ex) {
//            // Catches any other unexpected exceptions
//            logger.error("Email-Service: An unexpected error occurred for {}: {}", to, ex.getMessage(), ex);
//        }
    }
}
