package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class EmailServiceController {


    private final EmailService emailService;

    @GetMapping("/send")
    public String sendEmail(){
        emailService.sendEmail("riteshkusingh27@gmail.com","Test Email","This is a test email from Spring Boot");
        return "Email Sent Successfully";
}



}
