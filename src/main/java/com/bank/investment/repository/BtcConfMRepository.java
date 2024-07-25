package com.bank.investment.repository;

import com.bank.investment.model.BtcConfM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BtcConfMRepository extends JpaRepository<BtcConfM, Long> {
    BtcConfM findTopByOrderByCreateDateDesc();
}
