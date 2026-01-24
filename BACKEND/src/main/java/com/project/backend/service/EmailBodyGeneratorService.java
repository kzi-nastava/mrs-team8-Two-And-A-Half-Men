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
    public String generatePassengerAddedEmail(String name, String url) {
        return "<table align='center' width='100%' cellpadding='0' cellspacing='0' style='padding:20px; background-color:#f4f6f8; font-family:Arial, Helvetica, sans-serif;'>" +
            "  <tr>" +
            "    <td align='center'>" +

            "      <table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff; border-radius:10px; box-shadow:0 4px 10px rgba(0,0,0,0.08); overflow:hidden;'>" +

            "        <tr>" +
            "          <td style='background-color:#2563eb; padding:20px; text-align:center;'>" +
            "            <h2 style='margin:0; color:#ffffff; font-size:22px;'>🚗 Ride Invitation</h2>" +
            "          </td>" +
            "        </tr>" +

            "        <tr>" +
            "          <td style='padding:30px; color:#333333; font-size:15px; line-height:1.6;'>" +
            "            <p style='margin-top:0;'>Hello</p>" +

            "            <p>" +
            "              <strong>" + name + "</strong> has added you as a passenger to their ride." +
            "            </p>" +

            "            <p>" +
            "              You can view the ride details and join using the button below." +
            "            </p>" +

            "            <div style='text-align:center; margin:30px 0;'>" +
            "              <a href='" + url + "' " +
            "                 style='background-color:#2563eb; color:#ffffff; text-decoration:none; padding:14px 28px; " +
            "                        border-radius:6px; font-weight:bold; display:inline-block;'>" +
            "                View Ride" +
            "              </a>" +
            "            </div>" +

            "            <p style='margin-bottom:0; font-size:13px; color:#777777;'>" +
            "              If you did not expect this invitation, you may safely ignore this email." +
            "            </p>" +
            "          </td>" +
            "        </tr>" +

            "        <tr>" +
            "          <td style='background-color:#f1f5f9; padding:15px; text-align:center; font-size:12px; color:#777777;'>" +
            "            This is an automated message. Please do not reply." +
            "          </td>" +
            "        </tr>" +

            "        <tr>" +
            "          <td style='background-color:#f1f5f9; padding:15px; text-align:center; font-size:12px; color:#777777;'>" +
            "            If the button doesn't work follow this link: " + url +
            "          </td>" +
            "        </tr>" +

            "      </table>" +

            "    </td>" +
            "  </tr>" +
            "</table>";

    }
}
