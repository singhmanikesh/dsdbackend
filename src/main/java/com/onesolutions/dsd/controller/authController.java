package com.onesolutions.dsd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesolutions.dsd.dto.*;

import com.onesolutions.dsd.dto.AddHpRequestDTO;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import com.onesolutions.dsd.service.userService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class authController {

    private final userService userService;
    private final ObjectMapper objectMapper;

    public authController(userService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(
            @RequestPart("userRequest") String userRequestJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        try {
            UserRequestDTO userRequest = objectMapper.readValue(userRequestJson, UserRequestDTO.class);

            userService.registerUser(userRequest, avatar);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Registered successfully"));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/admin/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterRequestDTO adminRequest) {
        try {
            userService.registerAdmin(adminRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Admin registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/hp")
    public String addHpToUser(
            @PathVariable Long id,
            @RequestBody AddHpRequestDTO request) {

        userService.addHp(id, request.getHp());

        return "HP updated successfully";
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

    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody AuthDto userRequest) {

        try {

            Map<String, Object> response =
                    userService.authenticateAdminAndgenerateToken(userRequest);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
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
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequestDTO request) {

        try {

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Passwords do not match"));
            }

            userService.resetPassword(
                    request.getToken(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );

            return ResponseEntity.ok(
                    Map.of("message", "Password reset successfully")
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}