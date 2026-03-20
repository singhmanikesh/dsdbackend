package com.onesolutions.dsd.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String gamerName;
    private String steamid;
    private String riotid;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private Roles roles = Roles.USER;
    private Integer hp;
    // Fields for password reset
    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Column(name = "otp_verified")
    private Boolean otpVerified = false;

    @Column(name = "reset_token" , unique = true)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @ManyToMany(mappedBy = "usersJoined")
    private List<Tournament> tournamentsJoined = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (hp == null) {
            hp = 0; // Default HP value
        }
    }



}
