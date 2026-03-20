package com.onesolutions.dsd.serviceimpl;


import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.repository.profileRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class AppUserDetailsService implements UserDetailsService {
    public final profileRepo profileRepo;



    public AppUserDetailsService(profileRepo profileRepo) {
        this.profileRepo = profileRepo;

    }


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Login attempt: " + email);
        UserEntity user =  profileRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User with email "+email+" not found"));
        // Roles is an enum on the UserEntity; use its name() as the role string
        System.out.println("Login attempt: " + user);
        return User.builder()
                // username is the email in this case
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().name())// Assuming getRole() returns an enum value
                .build();
    }
}
