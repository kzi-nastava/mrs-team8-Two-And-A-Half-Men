package com.project.backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmailBodyGeneratorService {
    public String generateDriverActivationEmailBody(String driverName, String activationLink) {
        return "<html>" +
                "<body>" +
                "<h2>Welcome to Our Service, " + driverName + "!</h2>" +
                "<p>Thank you for registering as a driver. Please click the link below to activate your account:</p>" +
                "<a style=\"padding: 10px 20px; color: white; background-color: blue; text-decoration: underline;\" " +
                " href=\"" + activationLink + "\">Activate My Account</a>" +
                "<p>If you did not register for this account, please ignore this email.</p>" +
                "<br>" +
                "<p>Best regards,<br>The Team</p>" +
                "<br><br>" +
                "<p>If the button doesn't work follow this link: " + activationLink + "</p>" +
                "<footer style=\"font-size: small; color: gray;\">" +
                "This is an automated message, please do not reply." +
                "</footer>" +
                "</body>" +
                "</html>";
    }
}
