package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String subject, String body) {

        try {

            String fromEmail = "manikesh.amcec@gmail.com";

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("body", body);

            // Process the HTML template
            String htmlContent = templateEngine.process("registration-mail", context);

            // Create MimeMessage
            MimeMessage message = mailSender.createMimeMessage();

            // Helper for MIME message
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML email

            // Send email
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }
}