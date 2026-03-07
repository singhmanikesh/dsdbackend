package com.onesolutions.dsd.serviceimpl;

import com.onesolutions.dsd.dto.UserRequestDTO;
import com.onesolutions.dsd.dto.UserResponseDTO;
import com.onesolutions.dsd.entity.UserEntity;
import com.onesolutions.dsd.repository.profileRepo;
import com.onesolutions.dsd.service.userService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class userServiceImpl implements userService {

    private   final profileRepo profileRepo;



    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequest) {
        UserEntity newprofile = toEntity(userRequest);
        newprofile = profileRepo.save(newprofile);
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

}
