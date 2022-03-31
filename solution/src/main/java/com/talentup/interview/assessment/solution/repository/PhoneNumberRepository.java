package com.talentup.interview.assessment.solution.repository;

import com.talentup.interview.assessment.solution.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    boolean existsByNumberAndAccountId(String number, long accountId);
}
