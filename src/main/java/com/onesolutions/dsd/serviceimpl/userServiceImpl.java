package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.Utility.Jwtutil;
import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.repository.profileRepo;
import com.onesolutions.dsd.service.EmailService;
import com.onesolutions.dsd.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class userServiceImpl implements userService {

    private   final profileRepo profileRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;
    private final PasswordEncoder passwordEncoder;



    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequest) {

        if (profileRepo.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Account already registered with this email");
        }
        UserEntity newprofile = toEntity(userRequest);
        newprofile.setPassword(passwordEncoder.encode(newprofile.getPassword()));
        newprofile = profileRepo.save(newprofile);
        emailService.sendEmail(newprofile.getEmail(),"Welcome to DSD","Your account has been created successfully! Your gamer name is: " + newprofile.getGamerName());
        return toDto(newprofile);
    }

    public UserEntity toEntity(UserRequestDTO userRequest){
        UserEntity entity = new UserEntity();
        entity.setEmail(userRequest.getEmail());
        entity.setPassword(userRequest.getPassword());
        entity.setGamerName(userRequest.getGamername());
        entity.setRiotid(userRequest.getRiotid());
        entity.setSteamid(userRequest.getSteamid());
        entity.setAvatarUrl(userRequest.getAvatarurl());
        return entity;
    }

    public UserResponseDTO toDto(UserEntity userEntity){
        return UserResponseDTO.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .gamerName(userEntity.getGamerName())
                .riotId(userEntity.getRiotid())
                .steamId(userEntity.getSteamid())
                .avatarUrl(userEntity.getAvatarUrl())
                .hp(userEntity.getHp())
                .build();
    }

    public UserEntity getcurrentProfile(){
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        // return the current profile details from the database
        return profileRepo.findByEmail(auth.getName()).orElseThrow(()->new RuntimeException("Profile not found"));
    }
    // public profile is the profile of any user that is not the current user, it can be accessed by email
    public UserResponseDTO getpublicProfile(String email){

        UserEntity current;

        if(email == null){
            current = getcurrentProfile();
            System.out.println(current.getGamerName());
            System.out.println(current.getEmail());
        }
        else{
            current = profileRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("profile is not found with email " + email));
        }

        return toDto(current);
    }

    public Map<String, Object> authenticateAndgenerateToken(AuthDto authdto) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authdto.getEmail(), authdto.getPassword()));
            // generate jwt token
//            String token = jwtutil.generateToken(authdto.getEmail());
            System.out.println("Authentication successful for email: " + authdto.getEmail());
            System.out.println("Authentication successful for email: " + authdto.getEmail());
            String accesstoken = jwtutil.generateAccessToken(authdto.getEmail());
            String refreshtoken = jwtutil.generateRefreshToken(authdto.getEmail());
            System.out.println("Generated access token: " + accesstoken);
            System.out.println("Generated referesh token: " + refreshtoken);
            return Map.of(
                    "accesstoken" , accesstoken,
                    "refreshtoken" , refreshtoken,
                    "user" , getpublicProfile(authdto.getEmail())
            );
        } catch (Exception e){
            e.printStackTrace();

            throw new RuntimeException("Invalid email or Password");
        }
    }

    @Override
    public void forgotPassword(String email) {

        UserEntity user = profileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        /// 1 minute cooldown before requesting another OTP
        if(user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(LocalDateTime.now().minusMinutes(1))){
            throw new RuntimeException("Please wait before requesting another OTP");
        }

        // generate 4 digit OTP
        String otp = String.valueOf((int)(Math.random() * 9000) + 1000);

        // set OTP fields
        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setOtpVerified(false);

        // clear previous reset tokens if any
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        profileRepo.save(user);

        // send email
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset OTP",
                "Your OTP for password reset is: " + otp + ". It will expire in 10 minutes."
        );
    }

    @Override
    public String verifyOtp(String email, String otp) {

        UserEntity user = profileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if(user.getResetOtp() == null){
            throw new RuntimeException("OTP not requested");
        }

        if(!user.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(user.getOtpExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP expired");
        }

        user.setOtpVerified(true);

        String resetToken = UUID.randomUUID().toString();

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        profileRepo.save(user);

        return resetToken;
    }

    @Override
    public void resetPassword(String token, String newPassword) {

    }


    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        UserEntity user = profileRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        // clear reset data
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        user.setOtpVerified(false);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        profileRepo.save(user);

    }


}
