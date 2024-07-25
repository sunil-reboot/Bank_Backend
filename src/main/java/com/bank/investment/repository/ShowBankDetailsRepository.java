package com.bank.investment.repository;

import com.bank.investment.model.ShowBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowBankDetailsRepository extends JpaRepository<ShowBankDetails, Long> {

    @Query(value = "SELECT * FROM show_bank_details s where date = (select max(date) from show_bank_details) ORDER BY s.date DESC", nativeQuery = true)
    ShowBankDetails findLatest();
}
