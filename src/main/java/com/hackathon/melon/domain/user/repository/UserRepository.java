package com.hackathon.melon.domain.user.repository;

import com.hackathon.melon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGithubId(Long githubId);
}
