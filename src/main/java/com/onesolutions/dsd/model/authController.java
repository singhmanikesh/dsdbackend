package com.onesolutions.dsd.model;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class authController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }



}
