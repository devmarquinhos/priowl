package com.devmarquinhos.priowl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        // Todo: change from localhost to url
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Priowl 🦉 - Password Rest");
        message.setText("Greetings!\n\n" +
                "We noticed you requested a password reset for your Priowl account.\n" +
                "Click in the link below to change your passsord:\n\n" +
                resetLink + "\n\n" +
                "This link will expire after 15 minutes.\n" +
                "If you didnt request the password reset, please ignore this email.\n\n" +
                "Priowl team.");

        mailSender.send(message);
    }
}
