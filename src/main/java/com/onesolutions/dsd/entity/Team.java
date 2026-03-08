package com.onesolutions.dsd.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    private String teamName;

    private Long tournamentId;

    private String teamLeaderGamerName;

}