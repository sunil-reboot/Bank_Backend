package com.bank.investment.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bank.investment.model.AvailableBtcQty;
import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import com.bank.investment.model.BtcConfM;
import com.bank.investment.model.WithdrawalDetailsT;
import com.bank.investment.repository.AvailableBtcQtyRepository;
import com.bank.investment.repository.BtcBuyAndWithdrawDetailsRepository;
import com.bank.investment.repository.BtcConfMRepository;
import javax.transaction.Transactional;

import com.bank.investment.repository.WithdrawBtcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class BtcBuyServiceImpl implements BtcBuyService {

    @Autowired
    private BtcConfMRepository btcConfMRepository;
    @Autowired
    private BtcBuyAndWithdrawDetailsRepository btcBuyAndWithdrawDetailsRepository;
    @Autowired
    private AvailableBtcQtyRepository availableBtcQtyRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private WithdrawBtcRepository withdrawalDetailsTRepository;

    private static final String BUCKET_NAME = "customer-btc-details";

    @Override
    public BtcBuyAndWithdrawDetails saveBtcBuyDetails(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        BtcConfM btcConfM = btcConfMRepository.findTopByOrderByCreateDateDesc();
        BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails1 = btcBuyAndWithdrawDetailsRepository.save(
                buildBtcBuyAndWithdrawDetails(btcBuyAndWithdrawDetails, btcConfM.getBtcRate(), btcConfM.getId()));
        return btcBuyAndWithdrawDetails1;
    }

    @Override
    @Transactional
    public boolean approveOrRejectBtcBuyDetails(long phNo, BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails, boolean isApproved, boolean isRejected, String cashierDetails) {
        Timestamp approveDate = null;
        Timestamp rejectDate = null;
        if(isApproved) {
            approveDate = Timestamp.from(Instant.now());
        }
        String utrNo = "";
        if (!isApproved && isRejected) {
            rejectDate = Timestamp.from(Instant.now());
            utrNo = String.valueOf(Timestamp.from(Instant.now()));

        }
        if(isApproved) {
            utrNo = btcBuyAndWithdrawDetails.getUtrNumber();
            saveAvailableBtcQty(btcBuyAndWithdrawDetails);
        }
        log.info("We  are rejecting the records. Updaing available balance amt");
//        if(isRejected) {
//            saveAvailableBtcQtyForRejected(btcBuyAndWithdrawDetails);
//        }
        log.info("We  are rejecting the records. Updaing available balance amt done 1111");

        btcBuyAndWithdrawDetailsRepository.approveOrRejectBuyBtc(isApproved, isRejected, approveDate, rejectDate, cashierDetails, btcBuyAndWithdrawDetails.getId(), utrNo);
        return true;
    }

    @Override
    public BtcBuyAndWithdrawDetails saveUser(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) throws Exception {


//        File file = convertMultiPartToFile(multipartFile);
//        String fileName = UUID.randomUUID().toString();
//        try {
//            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file));
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Exception("Error uploading file to S3");
//        } finally {
//            if(file != null) {
//                file.delete();
//            }
//        }
        return btcBuyAndWithdrawDetailsRepository.save(btcBuyAndWithdrawDetails);
    }

    @Override
    @Transactional
    public BtcBuyAndWithdrawDetails cashierPayment(long phoneNumber, long amount, String utrNumber) {
        log.info("We are in service cashierPayment");
        String pNo = String.valueOf(phoneNumber);
        if(pNo.trim().length() == 10) {
            pNo = "91"+pNo;
        }
        List<BtcBuyAndWithdrawDetails> btcBuyAndWithdrawDetails = btcBuyAndWithdrawDetailsRepository.findByPhoneNumber(Long.parseLong(pNo));
        if(btcBuyAndWithdrawDetails == null || btcBuyAndWithdrawDetails.size() == 0) {
            return null;
        }
        BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails1 = btcBuyAndWithdrawDetailsRepository.save(
                    buildBtcBuyAndWithdrawalDetails(Long.parseLong(pNo), amount, utrNumber));
        AvailableBtcQty availableBtcQty = availableBtcQtyRepository.findByPhoneNumber(Long.parseLong(pNo));

        long balanceAmount = availableBtcQty.getAvailableBalance() - amount;
        if (balanceAmount < 0) {
            log.info("Available balance is negative");
            return null;
        }
        availableBtcQtyRepository.updateAvailableBtcQty(Timestamp.from(Instant.now()), balanceAmount, availableBtcQty.getId());
        return btcBuyAndWithdrawDetails1;
    }

    private BtcBuyAndWithdrawDetails
    buildBtcBuyAndWithdrawalDetails(long phoneNumber, long amount, String utrNumber) {
        WithdrawalDetailsT withdrawalDetailsT = withdrawalDetailsTRepository.getUserBankDetailsByPhoneNumber(phoneNumber);
        BtcBuyAndWithdrawDetails b1 =  BtcBuyAndWithdrawDetails.builder()
                .UtrNumber(utrNumber)
                .phoneNumber(phoneNumber)
                .withdrawAmount(amount)
                .withdrawPersonDetails("Cashier")
                .type("WITHDRAW")
                .isApproved(true)
                .approvalDate(Timestamp.from(Instant.now()))
                .buyDate(Timestamp.from(Instant.now()))
                .bankDetailsId(withdrawalDetailsT.getId())
                .build();
        log.info("THe record that we are saving is {}",b1.toString());
        return b1;
    }

    // check here. While updating correct approved amount is not getting deducted
    @Transactional
    private void saveAvailableBtcQty(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        AvailableBtcQty availableBtcQty = availableBtcQtyRepository.findByPhoneNumber(btcBuyAndWithdrawDetails.getPhoneNumber());
        //BtcConfM btcConfM = btcConfMRepository.findTopByOrderByCreateDateDesc();
        if(availableBtcQty != null && availableBtcQty.getPhoneNumber() > 0) {
            long availableBal = (availableBtcQty.getPhoneNumber() > 0)
                    ? availableBtcQty.getAvailableBalance() + ((long) (btcBuyAndWithdrawDetails.getBtcQty() * btcBuyAndWithdrawDetails.getBtcRate()))
                    : ((long) (btcBuyAndWithdrawDetails.getBtcQty() * btcBuyAndWithdrawDetails.getBtcRate()));
            availableBtcQtyRepository.updateAvailableBtcQty(Timestamp.from(Instant.now()), availableBal, availableBtcQty.getId());
        } else {
            availableBtcQtyRepository.save(buildAvailableBtcQty(btcBuyAndWithdrawDetails.getBtcQty(), btcBuyAndWithdrawDetails.getPhoneNumber(), btcBuyAndWithdrawDetails.getBtcRate(), btcBuyAndWithdrawDetails.isApproved() ));
        }
    }

    private AvailableBtcQty buildAvailableBtcQty(int qty, long phoneNumber, double btcRate, boolean isApproved) {

        long totalAvailableBal = (long) (btcRate * qty);
        log.info("Total value is {}", totalAvailableBal);
        return AvailableBtcQty.builder()
                .createDate(Timestamp.from(Instant.now()))
                .btcQty(qty)
                .availableBalance(totalAvailableBal)
                .phoneNumber(phoneNumber)
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
                .type("BUY")
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
