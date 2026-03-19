package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    @Override
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {

            String fromEmail = "riteshkusingh27@gmail.com";

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariables(variables);

            // Process the HTML template
            String htmlContent = templateEngine.process(templateName, context);

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