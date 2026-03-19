package com.onesolutions.dsd.dto;

import jakarta.mail.Multipart;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequestDTO {



        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        private String gamername;

        private String steamid;

        private String riotid;

        private MultipartFile avatar;

//        private Roles role;

}
