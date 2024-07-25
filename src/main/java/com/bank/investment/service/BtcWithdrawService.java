package com.bank.investment.service;

import com.bank.investment.model.BtcBuyAndWithdrawDetails;

public interface BtcWithdrawService {

    public BtcBuyAndWithdrawDetails saveBtcWithdrawDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails);
    public boolean approveOrRejectBtcWithdrawDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails, boolean isApproved, boolean isRejected, String cashierDetails);
}
