package com.onesolutions.dsd.service;

import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;

import java.util.Map;

public interface userService {

    UserResponseDTO registerUser(UserRequestDTO userRequest);

    Map<String, Object> authenticateAndgenerateToken(AuthDto authdto);
        void forgotPassword(String email);
         String verifyOtp(String email, String otp);
        void resetPassword(String token, String newPassword);

    void resetPassword(String token, String newPassword, String confirmPassword);

//    void resetPassword(String email, String newPassword, String confirmPassword);
}