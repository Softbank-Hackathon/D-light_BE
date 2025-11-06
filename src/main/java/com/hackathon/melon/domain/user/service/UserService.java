package com.hackathon.melon.domain.user.service;

import com.hackathon.melon.domain.user.dto.UserResponse;
import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * GitHub ID로 현재 로그인한 사용자 정보 조회
     *
     * @param githubId GitHub 사용자 ID
     * @return 사용자 정보 DTO
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public UserResponse getCurrentUser(Long githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. GitHub ID: " + githubId));

        log.debug("현재 사용자 조회 성공: {}", user.getLogin());
        return UserResponse.from(user);
    }

    /**
     * 사용자 ID로 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 DTO
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        log.debug("사용자 조회 성공: {}", user.getLogin());
        return UserResponse.from(user);
    }
}