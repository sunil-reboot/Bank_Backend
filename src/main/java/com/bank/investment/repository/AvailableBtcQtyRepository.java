package com.bank.investment.repository;

import com.bank.investment.model.AvailableBtcQty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public interface AvailableBtcQtyRepository extends JpaRepository<AvailableBtcQty, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE available_btc_qty SET updated_date = ?1, available_balance=?2 WHERE id = ?3", nativeQuery = true)
    void updateAvailableBtcQty(Timestamp updateDate, long availableBal, long id);

    AvailableBtcQty findByPhoneNumber(long phoneNumber);
}
