package com.bank.investment.repository;

import com.bank.investment.model.AvailableBtcQty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FindUnApprovedUsersRepository extends JpaRepository<AvailableBtcQty, Long> {

    @Query(value = "SELECT * FROM entreaty.available_btc_qty a where a.available_balance > 0", nativeQuery = true)
    List<AvailableBtcQty> getAllUnapprovedUsers();

}
