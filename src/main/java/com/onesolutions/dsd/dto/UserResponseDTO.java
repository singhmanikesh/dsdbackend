package com.onesolutions.dsd.dto;

import com.onesolutions.dsd.entity.Roles;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {

    private Long id;

    private String email;

    private String gamerName;

    private String steamId;

    private String riotId;

    private String avatarUrl;

    private int hp;


}