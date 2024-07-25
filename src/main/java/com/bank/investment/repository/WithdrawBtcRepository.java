package com.bank.investment.repository;

import com.bank.investment.model.WithdrawalDetailsT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawBtcRepository extends JpaRepository<WithdrawalDetailsT, Long> {

    @Query(value = "select * from withdrawal_details_t where phone_number = ?1 and create_date = (select max(create_date) from withdrawal_details_t where phone_number = ?1)", nativeQuery = true)
    WithdrawalDetailsT getUserBankDetailsByPhoneNumber(long phoneNumber);

}
