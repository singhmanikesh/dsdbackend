package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.dto.ContactFormDTO;
import com.onesolutions.dsd.service.ContactFormService;
import com.onesolutions.dsd.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactFormServiceImpl implements ContactFormService {

    private final EmailService emailService;

    @Override
    public void sendContactForm(ContactFormDTO contactFormDTO) {
        try {
            log.info("Contact form submission received from: {} ({})", contactFormDTO.getName(), contactFormDTO.getEmail());

            // Recipient email
            String recipientEmail = "adminhelpdsd@gmail.com";

            log.debug("Contact form details - Name: {}, Email: {}, Topic: {}, Consent: {}",
                    contactFormDTO.getName(),
                    contactFormDTO.getEmail(),
                    contactFormDTO.getTopic(),
                    contactFormDTO.getConsent());

            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", contactFormDTO.getName());
            variables.put("email", contactFormDTO.getEmail());
            variables.put("topic", contactFormDTO.getTopic());
            variables.put("message", contactFormDTO.getMessage());
            variables.put("consent", contactFormDTO.getConsent() != null && contactFormDTO.getConsent());

            log.info("Preparing to send contact form email to: {}", recipientEmail);

            // Send email with subject as topic
            emailService.sendTemplateEmail(
                    recipientEmail,
                    contactFormDTO.getTopic(),
                    "contact-form-mail",
                    variables
            );

            log.info("Contact form email sent successfully to: {} with subject: {}", recipientEmail, contactFormDTO.getTopic());

        } catch (Exception e) {
            log.error("Error sending contact form email from: {} - Error: {}",
                    contactFormDTO.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

}

