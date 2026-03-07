package com.onesolutions.dsd.entity;

import jakarta.persistence.*;
import lombok.*;
import com.onesolutions.dsd.enums.GameCategory;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tournamentId;

    private String tournamentName;

    @Enumerated(EnumType.STRING)
    private GameCategory tournamentCategory;

    @CreationTimestamp
    private LocalDateTime tournamentCreated;

    private LocalDateTime tournamentExpiry;

    private Integer tournamentPrize;

    @Builder.Default
    private Integer totalJoined = 0;

    private String organizerName;

    private String gameName;

    @Builder.Default
    private Boolean expired = false;

    @Column(length = 2000)
    private String description;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "tournament_users",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> usersJoined = new ArrayList<>();
}