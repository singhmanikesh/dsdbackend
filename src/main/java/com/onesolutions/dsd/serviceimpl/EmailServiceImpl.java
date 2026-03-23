package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    /**
     * Async method for non-blocking email sending (registration, contact form)
     * Returns immediately, email is sent in a background thread
     */
    @Override
    @Async
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            log.info("Starting async email send to: {} with subject: {}", to, subject);
            sendEmailInternal(to, subject, templateName, variables);
            log.info("Email sent successfully (async) to: {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Error sending async email to: {} - Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Sync method for blocking email sending (forgot-password OTP)
     * Waits for email to complete before returning
     */
    @Override
    public void sendTemplateEmailSync(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            log.info("Starting synchronous email send to: {} with subject: {}", to, subject);
            sendEmailInternal(to, subject, templateName, variables);
            log.info("Email sent successfully (sync) to: {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Error sending sync email to: {} - Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Internal method that handles actual email sending logic
     * Used by both async and sync methods
     */
    private void sendEmailInternal(String to, String subject, String templateName, Map<String, Object> variables) throws Exception {
        String fromEmail = "adminhelpdsd@gmail.com";

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
    }
}