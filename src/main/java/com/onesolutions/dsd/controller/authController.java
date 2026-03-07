package com.onesolutions.dsd.controller;


import com.onesolutions.dsd.dto.AddHpRequestDTO;
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


    @PatchMapping("/users/{id}/hp")
    public String addHpToUser(
            @PathVariable Long id,
            @RequestBody AddHpRequestDTO request) {

        userService.addHp(id, request.getHp());

        return "HP added successfully";
    }
}
