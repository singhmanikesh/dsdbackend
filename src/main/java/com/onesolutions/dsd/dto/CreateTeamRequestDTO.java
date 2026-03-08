package com.onesolutions.dsd.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateTeamRequestDTO {

    private Long tournamentId;

    private String teamLeaderGamerName;

    private String teamName;

    private List<String> gamerNames;

}