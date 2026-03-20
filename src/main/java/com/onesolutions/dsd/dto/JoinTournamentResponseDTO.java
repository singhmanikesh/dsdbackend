package com.onesolutions.dsd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinTournamentResponseDTO {

    private Long tournamentId;
    private String tournamentName;
    private Long userId;
    private String userEmail;
    private String gamerName;
    private String message;
    private LocalDateTime joinedAt;
    private Integer totalJoinedNow;

}

