package com.bank.investment.repository;


import com.bank.investment.model.BtcConfM;
import com.bank.investment.model.FundsDetailsT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundDetailsRepository extends JpaRepository<FundsDetailsT, Long> {

    FundsDetailsT findByName(String name);
}
