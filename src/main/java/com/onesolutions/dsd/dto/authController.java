package com.onesolutions.dsd.dto;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class authController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }



}
