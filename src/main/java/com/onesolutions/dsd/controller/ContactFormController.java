package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.ContactFormDTO;
import com.onesolutions.dsd.service.ContactFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactFormController {

    private final ContactFormService contactFormService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactFormDTO contactFormDTO) {
        try {
            log.info("Contact form endpoint called - Submitter: {}", contactFormDTO.getName());
            contactFormService.sendContactForm(contactFormDTO);
            log.info("Contact form processed successfully for: {}", contactFormDTO.getEmail());
            return ResponseEntity.ok(Map.of("message", "Contact form submitted successfully"));
        } catch (Exception e) {
            log.error("Failed to process contact form for: {} - Error: {}", 
                    contactFormDTO.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}

