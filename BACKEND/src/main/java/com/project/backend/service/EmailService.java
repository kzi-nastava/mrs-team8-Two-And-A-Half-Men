package com.project.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendEmail(String toAddress, String subject, String body) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true , "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }
    // Create method for building mail u need to have subject body and address you need to send to
    public void sendVerificationEmail(String toAddress, String Username, String verificationLink) throws Exception {
        String subject = "Please verify your email address";
        String body = "Hello " + Username + ",<br><br>"
                + "Thank you for registering. Please click the link below to verify your email address:<br>"
                + "<a href=\"" + verificationLink + "\">Verify Email</a><br><br>"
                + "If you did not register, please ignore this email.<br><br>"
                + "Best regards,<br>"
                + "The Team";
        sendEmail(toAddress, subject, body);
    }

}
