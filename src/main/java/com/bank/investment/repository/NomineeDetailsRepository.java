package com.bank.investment.repository;

import com.bank.investment.model.NomineeDetailsT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NomineeDetailsRepository  extends JpaRepository<NomineeDetailsT, Long> {

    NomineeDetailsT findByEmail(String email);
    NomineeDetailsT findByUrl(String url);
}
