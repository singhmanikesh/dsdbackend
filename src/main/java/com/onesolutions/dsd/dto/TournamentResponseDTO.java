package com.onesolutions.dsd.dto;

import com.onesolutions.dsd.entity.GameCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TournamentResponseDTO {

    private Long tournamentId;

    private String tournamentName;

    private GameCategory tournamentCategory;

    private LocalDateTime tournamentCreated;

    private LocalDateTime tournamentExpiry;

    private Integer tournamentPrize;

    private Integer totalJoined;

    private String organizerName;

    private String gameName;

    private String description;

}