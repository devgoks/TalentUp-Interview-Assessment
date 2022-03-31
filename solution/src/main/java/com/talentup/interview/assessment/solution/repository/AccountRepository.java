package com.talentup.interview.assessment.solution.repository;

import com.talentup.interview.assessment.solution.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsernameAndAuthId(String username, String authId);
}
