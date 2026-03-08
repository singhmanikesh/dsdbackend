package com.onesolutions.dsd.controller;

import com.onesolutions.dsd.dto.*;
import com.onesolutions.dsd.service.userService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
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
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userRequest) {

        try {

            userService.registerUser(userRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Registered successfully"));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto userRequest) {

        try {

            Map<String, Object> response =
                    userService.authenticateAndgenerateToken(userRequest);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }


    @PostMapping("/forget-password")
    public ResponseEntity<Map<String, Object>> forgetPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {

        try {

            String email = forgotPasswordRequestDTO.getEmail();

            userService.forgotPassword(email);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Password reset OTP sent to " + email
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of("error", e.getMessage())
                    );
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {

        try {

            String token = userService.verifyOtp(
                    verifyOtpRequestDTO.getEmail(),
                    verifyOtpRequestDTO.getOtp()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message", "OTP verified successfully",
                            "resetToken", token
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String,Object>> resetPassword(@RequestBody ResetPasswordRequestDTO request){

        try{

            if(!request.getNewPassword().equals(request.getConfirmPassword())){
                return ResponseEntity.badRequest()
                        .body(Map.of("error","Passwords do not match"));
            }

            userService.resetPassword(
                    request.getToken(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );

            return ResponseEntity.ok(
                    Map.of("message","Password reset successfully")
            );

        }catch(RuntimeException e){

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error",e.getMessage()));
        }
    }
}