package com.bank.investment.repository;

import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BtcBuyAndWithdrawDetailsRepository extends JpaRepository<BtcBuyAndWithdrawDetails, Long> {

    @Query(value = "SELECT * FROM ( SELECT * FROM entreaty.btc_buy_withdraw_details where phone_number=?1 ORDER BY buy_date DESC LIMIT 10) AS sub ORDER BY buy_date DESC ", nativeQuery = true)
    List<BtcBuyAndWithdrawDetails> getLast10BtcDetails(Long phoneNumber);

    @Query(value = "SELECT * FROM btc_buy_withdraw_details where is_approved=false and is_rejected=false ORDER BY buy_date ASC ", nativeQuery = true)
    List<BtcBuyAndWithdrawDetails> getApprovalDetails();

    @Modifying
    @Query(value = "UPDATE btc_buy_withdraw_details SET is_approved = :isApproved, is_rejected = :isRejected, approval_date = :approvalDate, rejected_date = :rejectedDate, cashier_details = :cashierDetails, utr_number = :utrNo WHERE id = :id", nativeQuery = true)
    void approveOrRejectBuyBtc(@Param("isApproved") boolean isApproved,
                               @Param("isRejected") boolean isRejected,
                               @Param("approvalDate") Timestamp approvalDate,
                               @Param("rejectedDate") Timestamp rejectedDate,
                               @Param("cashierDetails") String cashierDetails,
                               @Param("id") long id,
                               @Param("utrNo") String utrNo);


    List<BtcBuyAndWithdrawDetails> findByPhoneNumber(long phoneNumber);

    @Modifying
    @Query(value="select * from btc_buy_withdraw_details where phone_number=?1 and date(buy_date) = current_date", nativeQuery = true)
    List<BtcBuyAndWithdrawDetails> getTodaysList(long phoneNumber);

    @Query(value="select * from btc_buy_withdraw_details where phone_number=?1 and buy_date = (select max(buy_date) from btc_buy_withdraw_details where phone_number=?1 and is_approved = true or is_rejected = true)", nativeQuery = true)
    BtcBuyAndWithdrawDetails getLatestApprovedOrRejectedDetails(long phoneNo);

}
