package com.onesolutions.dsd.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserJoinedResponseDTO {

    private Long userId;
    private String email;
    private String gamerName;
    private Integer hp;

}