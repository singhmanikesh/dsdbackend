package com.onesolutions.dsd.controller;


import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import com.onesolutions.dsd.service.userService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
public class authController {

    private final userService userService;

    public authController(userService userService) {
        this.userService = userService;
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequest){
        UserResponseDTO resp =   userService.registerUser(userRequest);
        return resp ;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthDto userRequest){
        // Implement login logic here
        return "Login successful!";
    }

}
