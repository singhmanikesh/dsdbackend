package com.onesolutions.dsd.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String token;
    private String newPassword;
    private String confirmPassword;


}