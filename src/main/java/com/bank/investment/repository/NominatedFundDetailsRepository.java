package com.bank.investment.repository;

import com.bank.investment.model.NominatedDetailsT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NominatedFundDetailsRepository extends JpaRepository<NominatedDetailsT, Long> {
}
