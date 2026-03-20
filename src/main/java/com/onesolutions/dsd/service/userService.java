package com.onesolutions.dsd.service;

import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.dto.AdminRegisterRequestDTO;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface userService {

    UserResponseDTO registerUser(UserRequestDTO userRequest, MultipartFile avatar);

    UserResponseDTO registerAdmin(AdminRegisterRequestDTO adminRequest);

    Map<String, Object> authenticateAndgenerateToken(AuthDto authdto);

    Map<String, Object> authenticateAdminAndgenerateToken(AuthDto authdto);

    void addHp(Long userId, Integer hp);

    void forgotPassword(String email);

    String verifyOtp(String email, String otp);

    void resetPassword(String token, String newPassword);

    void resetPassword(String token, String newPassword, String confirmPassword);

    List<UserResponseDTO> getAllUsers();

    //    void resetPassword(String email, String newPassword, String confirmPassword);
    String uploadToR2(MultipartFile file) throws IOException;
}