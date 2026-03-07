package com.onesolutions.dsd.dto;

import com.onesolutions.dsd.enums.GameCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentRequestDTO {

    private String tournamentName;

    private GameCategory tournamentCategory;

    private LocalDateTime tournamentExpiry;

    private Integer tournamentPrize;

    private String organizerName;

    private String gameName;

    private String description;

}