package com.onesolutions.dsd.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamResponseDTO {

    private String teamName;

    private String teamLeader;

    private List<String> players;

}