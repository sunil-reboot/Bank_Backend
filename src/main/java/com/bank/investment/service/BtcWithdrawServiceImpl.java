package com.bank.investment.service;

import com.bank.investment.model.AvailableBtcQty;
import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import com.bank.investment.model.BtcConfM;
import com.bank.investment.repository.AvailableBtcQtyRepository;
import com.bank.investment.repository.BtcConfMRepository;
import com.bank.investment.repository.BtcBuyAndWithdrawDetailsRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class BtcWithdrawServiceImpl implements BtcWithdrawService {
    @Autowired
    private BtcConfMRepository btcConfMRepository;
    @Autowired
    private BtcBuyAndWithdrawDetailsRepository buyBtcRepository;
    @Autowired
    private AvailableBtcQtyRepository availableBtcQtyRepository;

    @Override
    public BtcBuyAndWithdrawDetails saveBtcWithdrawDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        BtcConfM btcConfM = btcConfMRepository.findTopByOrderByCreateDateDesc();
        BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails1 = buyBtcRepository.save(buildBtcBuyAndWithdrawDetails(btcBuyAndWithdrawDetails, btcConfM.getBtcRate(), btcConfM.getId()));
        return btcBuyAndWithdrawDetails1;
    }

    @Override
    @Transactional
    public boolean approveOrRejectBtcWithdrawDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails, boolean isApproved, boolean isRejected, String cashierDetails) {
        AvailableBtcQty availableBtcQty = getAvailableBtcQtyDetails(btcBuyAndWithdrawDetails);
        Timestamp approveDate = null;
        Timestamp rejectDate = null;
        String utrNo="";
        if(isApproved) {
            utrNo = btcBuyAndWithdrawDetails.getUtrNumber();
            approveDate = Timestamp.from(Instant.now());
        }

        if (!isApproved && isRejected) {
            utrNo = String.valueOf(Timestamp.from(Instant.now()));
            rejectDate = Timestamp.from(Instant.now());
        }
        if (availableBtcQty == null) {
            availableBtcQtyRepository.save(buildAvailableBtcQty(btcBuyAndWithdrawDetails));
        } else {
            int qty = availableBtcQty.getBtcQty() + btcBuyAndWithdrawDetails.getBtcQty();
            BtcConfM btcConfm =btcConfMRepository.findTopByOrderByCreateDateDesc();
            long totalValue = (long) (btcConfm.getBtcRate() * btcBuyAndWithdrawDetails.getBtcQty());
            availableBtcQtyRepository.updateAvailableBtcQty(Timestamp.from(Instant.now()), totalValue, availableBtcQty.getId());
        }
        buyBtcRepository.approveOrRejectBuyBtc(isApproved, isRejected, approveDate, rejectDate, cashierDetails, btcBuyAndWithdrawDetails.getId(), utrNo);
        return true;
    }

    private AvailableBtcQty buildAvailableBtcQty(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        return AvailableBtcQty.builder()
                .btcQty(btcBuyAndWithdrawDetails.getBtcQty())
                .phoneNumber(btcBuyAndWithdrawDetails.getPhoneNumber())
                .createDate(Timestamp.from(Instant.now()))
                .build();
    }

    private BtcBuyAndWithdrawDetails buildBtcBuyAndWithdrawDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails,
                                                                   double btcRate, long btcId) {
        return BtcBuyAndWithdrawDetails.builder()
                .btcId(btcId)
                .phoneNumber(btcBuyAndWithdrawDetails.getPhoneNumber())
                .btcRate(btcRate)
                .btcQty(btcBuyAndWithdrawDetails.getBtcQty())
                .buyDate(Timestamp.from(Instant.now()))
                .type("WITHDRAW")
                .build();
    }

    private AvailableBtcQty getAvailableBtcQtyDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        AvailableBtcQty availableBtcQty =  availableBtcQtyRepository.findByPhoneNumber(btcBuyAndWithdrawDetails.getPhoneNumber());
        if(availableBtcQty != null) {
            return availableBtcQty;
        } else {
            return null;
        }

    }
}
