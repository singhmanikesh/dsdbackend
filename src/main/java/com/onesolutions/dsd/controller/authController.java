package com.onesolutions.dsd.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class authController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }



}
