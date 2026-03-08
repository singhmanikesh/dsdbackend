package com.onesolutions.dsd.repository;

import com.onesolutions.dsd.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface profileRepo extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByResetToken(String resetToken);

}
