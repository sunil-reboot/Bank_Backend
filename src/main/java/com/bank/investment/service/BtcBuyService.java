package com.bank.investment.service;

import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import org.springframework.web.multipart.MultipartFile;

public interface BtcBuyService {

    BtcBuyAndWithdrawDetails saveUser(BtcBuyAndWithdrawDetails showBankDetails) throws Exception;
    BtcBuyAndWithdrawDetails saveBtcBuyDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails);
    BtcBuyAndWithdrawDetails cashierPayment(long phoneNumber, long amount, String utrNumber);
    boolean approveOrRejectBtcBuyDetails(long phNo, BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails, boolean isApproved, boolean isRejected, String cashierDetails);
}
