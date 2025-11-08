package com.hackathon.melon.domain.auth.onboarding.repository;

import com.hackathon.melon.domain.auth.onboarding.entity.OnboardingRecord;
import com.hackathon.melon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRecordRepository extends JpaRepository<OnboardingRecord, Long> {
    Optional<OnboardingRecord> findByStackId(String stackId);
    List<OnboardingRecord> findAllByUser(User user);
    Optional<OnboardingRecord> findByUserAndAccountId(User user, String accountId);
}