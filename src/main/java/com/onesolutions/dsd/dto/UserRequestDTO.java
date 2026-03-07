package com.onesolutions.dsd.dto;

import com.onesolutions.dsd.entity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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

        private String avatarurl;

        private Roles role;

}
