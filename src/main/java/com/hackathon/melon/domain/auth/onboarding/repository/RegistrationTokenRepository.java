package com.hackathon.melon.domain.auth.onboarding.repository;

import com.hackathon.melon.domain.auth.onboarding.entity.RegistrationToken;
import com.hackathon.melon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    Optional<RegistrationToken> findByToken(String token);
    Optional<RegistrationToken> findByTokenAndUser(String token, User user);
}