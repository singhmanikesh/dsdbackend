package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.Utility.Jwtutil;
import com.onesolutions.dsd.dto.AdminRegisterRequestDTO;
import com.onesolutions.dsd.dto.AuthDto;
import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import com.onesolutions.dsd.entity.Roles;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.repository.profileRepo;
import com.onesolutions.dsd.service.EmailService;
import com.onesolutions.dsd.service.userService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class userServiceImpl implements userService {

    private final profileRepo profileRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;
    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;
    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;


    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequest, MultipartFile avatar) {
        log.info("Starting user registration for email: {}", userRequest.getEmail());

        if (profileRepo.findByEmail(userRequest.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already exists - {}", userRequest.getEmail());
            throw new RuntimeException("Account already registered with this email");
        }

        if (profileRepo.findByGamerName(userRequest.getGamername()).isPresent()) {
            log.warn("Registration failed: Gamer name already exists - {}", userRequest.getGamername());
            throw new RuntimeException("Gamer name already taken. Please choose a different gamer name.");
        }

        UserEntity newprofile = toEntity(userRequest);
        newprofile.setPassword(passwordEncoder.encode(newprofile.getPassword()));

        if (avatar != null && !avatar.isEmpty()) {
            try {
                log.info("Avatar file detected. Validating and uploading...");

                // Validate file type
                String contentType = avatar.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    log.warn("Invalid file type for avatar: {}", contentType);
                    throw new RuntimeException("Only image files are allowed for avatar");
                }

                // Validate file size (5MB limit)
                long fileSizeInBytes = avatar.getSize();
                long maxFileSize = 5 * 1024 * 1024; // 5MB
                if (fileSizeInBytes > maxFileSize) {
                    log.warn("Avatar file size exceeds limit: {} bytes", fileSizeInBytes);
                    throw new RuntimeException("Avatar file size must not exceed 5MB");
                }

                String imageUrl = uploadToR2(avatar);
                newprofile.setAvatarUrl(imageUrl);
                log.info("Avatar uploaded successfully: {}", imageUrl);

            } catch (RuntimeException e) {
                log.error("Avatar validation failed: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Image upload failed", e);
                throw new RuntimeException("Image upload failed: " + e.getMessage());
            }
        }

        newprofile = profileRepo.save(newprofile);
        log.info("User saved to database with ID: {}", newprofile.getId());

        emailService.sendTemplateEmail(
                newprofile.getEmail(),
                "Welcome to DSD",
                "registration-mail",
                Map.of("body", "Your account has been created successfully! Your gamer name is: " + newprofile.getGamerName())
        );
        log.info("Welcome email sent to: {}", newprofile.getEmail());

        log.info("User registration completed successfully for: {}", userRequest.getEmail());
        return toDto(newprofile);
    }

    @Override
    public UserResponseDTO registerAdmin(AdminRegisterRequestDTO adminRequest) {
        log.info("Starting admin registration for email: {}", adminRequest.getEmail());

        if (profileRepo.findByEmail(adminRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Account already registered with this email");
        }

        boolean isAdmin = Boolean.TRUE.equals(adminRequest.getIsAdmin());
        boolean isOwner = Boolean.TRUE.equals(adminRequest.getIsOwner());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("At least one of isAdmin or isOwner must be true");
        }

        UserEntity adminUser = new UserEntity();
        adminUser.setEmail(adminRequest.getEmail());
        adminUser.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        adminUser.setGamerName(adminRequest.getGamername());
        adminUser.setRoles(isOwner ? Roles.OWNER : Roles.ADMIN);

        adminUser = profileRepo.save(adminUser);

        emailService.sendTemplateEmail(
                adminUser.getEmail(),
                "Welcome to DSD",
                "registration-mail",
                Map.of("body", "Your account has been created successfully! Your gamer name is: " + adminUser.getGamerName())
        );

        return toDto(adminUser);
    }

    @Override
    public void addHp(Long userId, Integer hp) {

        UserEntity user = profileRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // PATCH now updates HP to the provided value instead of incrementing it.
        user.setHp(hp);

        profileRepo.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return profileRepo.findAll()
                .stream()
                    .filter(user -> user.getRoles() != Roles.ADMIN && user.getRoles() != Roles.OWNER)
                .map(this::toDto)
                .toList();
    }

    public UserEntity toEntity(UserRequestDTO userRequest) {
        UserEntity entity = new UserEntity();
        entity.setEmail(userRequest.getEmail());
        entity.setPassword(userRequest.getPassword());
        entity.setGamerName(userRequest.getGamername());
        entity.setRiotid(userRequest.getRiotid());
        entity.setSteamid(userRequest.getSteamid());
        return entity;
    }

    public UserResponseDTO toDto(UserEntity userEntity) {
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

    public UserEntity getcurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }
        // return the current profile details from the database
        return profileRepo.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    // public profile is the profile of any user that is not the current user, it can be accessed by email
    public UserResponseDTO getpublicProfile(String email) {

        UserEntity current;

        if (email == null) {
            current = getcurrentProfile();
            System.out.println(current.getGamerName());
            System.out.println(current.getEmail());
        } else {
            current = profileRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("profile is not found with email " + email));
        }

        return toDto(current);
    }

    @Override
    public Map<String, Object> authenticateAndgenerateToken(AuthDto authdto) {
        try {
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
                    "accesstoken", accesstoken,
                    "refreshtoken", refreshtoken,
                    "user", getpublicProfile(authdto.getEmail())
            );
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", authdto.getEmail(), e);

            throw new RuntimeException("Invalid email or Password");
        }
    }

    @Override
    public Map<String, Object> authenticateAdminAndgenerateToken(AuthDto authdto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authdto.getEmail(), authdto.getPassword()));

            UserEntity user = profileRepo.findByEmail(authdto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (user.getRoles() != Roles.ADMIN && user.getRoles() != Roles.OWNER) {
                throw new RuntimeException("Only admin/owner accounts can login from this endpoint");
            }

            boolean isAdmin = user.getRoles() == Roles.ADMIN;
            boolean isOwner = user.getRoles() == Roles.OWNER;

            String accesstoken = jwtutil.generateAccessToken(authdto.getEmail());
            String refreshtoken = jwtutil.generateRefreshToken(authdto.getEmail());

            return Map.of(
                    "accesstoken", accesstoken,
                    "refreshtoken", refreshtoken,
                    "user", getpublicProfile(authdto.getEmail()),
                    "isAdmin", isAdmin,
                    "isOwner", isOwner
            );
        } catch (Exception e) {
            log.error("Admin authentication failed for email: {}", authdto.getEmail(), e);
            throw new RuntimeException("Invalid credentials or insufficient permissions");
        }
    }

    @Override
    public void forgotPassword(String email) {

        UserEntity user = profileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        /// 1 minute cooldown before requesting another OTP
        if (user.getOtpExpiry() != null && user.getOtpExpiry().isAfter(LocalDateTime.now().minusMinutes(1))) {
            throw new RuntimeException("Please wait before requesting another OTP");
        }

        // generate 4 digit OTP
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);

        // set OTP fields
        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setOtpVerified(false);

        // clear previous reset tokens if any
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        profileRepo.save(user);

        // send email
        emailService.sendTemplateEmail(
                user.getEmail(),
                "Password Reset OTP",
                "forgot-password-otp-mail",
                Map.of(
                        "otp", otp,
                        "expiryMinutes", 10
                )
        );
    }

    @Override
    public String verifyOtp(String email, String otp) {

        UserEntity user = profileRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (user.getResetOtp() == null) {
            throw new RuntimeException("OTP not requested");
        }

        if (!user.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
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
        resetPassword(token, newPassword, newPassword);
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

    // upload file to cloudfare R2 and return the url of the uploaded file
    @Override
    public String uploadToR2(MultipartFile file) throws IOException {
        try {
            log.info("Starting file upload to Cloudflare R2: {}", file.getOriginalFilename());

            String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            log.debug("Generated R2 key: {}", key);

            // Create upload request
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            log.debug("Uploading file to bucket: {}", bucketName);

            // Stream directly from request payload to avoid loading full file into heap.
            try (var inputStream = file.getInputStream()) {
                s3Client.putObject(
                        request,
                        RequestBody.fromInputStream(inputStream, file.getSize())
                );
            }

            log.debug("File successfully uploaded to R2");

            // Return full public URL
            String fullUrl = publicUrl + "/" + key;
            log.info("File upload completed. Public URL: {}", fullUrl);
            return fullUrl;

        } catch (IOException e) {
            log.error("Failed to read file bytes", e);
            throw e;
        } catch (Exception e) {
            log.error("Error uploading file to R2", e);
            throw new IOException("Failed to upload file to R2: " + e.getMessage(), e);
        }
    }


}
