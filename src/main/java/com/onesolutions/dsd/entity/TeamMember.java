package com.onesolutions.dsd.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}