package com.onesolutions.dsd.service;

import java.util.Map;

public interface EmailService {
    /**
     * Async email sending for non-blocking operations (registration, contact form)
     * This method returns immediately, email is sent in background
     */
    void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables);

    /**
     * Synchronous email sending that blocks until sent (forgot-password OTP)
     * This method waits for email to complete before returning
     */
    void sendTemplateEmailSync(String to, String subject, String templateName, Map<String, Object> variables);
}



