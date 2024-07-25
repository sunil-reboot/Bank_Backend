package com.bank.investment.controller;

import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import com.bank.investment.model.User;
import com.bank.investment.model.WithdrawalDetailsT;
import com.bank.investment.repository.AvailableBtcQtyRepository;
import com.bank.investment.repository.UserRepository;
import com.bank.investment.repository.WithdrawBtcRepository;
import com.bank.investment.service.BtcWithdrawService;
import com.bank.investment.util.CommonMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class WithdrawBtcController {

    private static final Logger log = LoggerFactory.getLogger(WithdrawBtcController.class);

    @Autowired
    private WithdrawBtcRepository withdrawBtcRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/saveBtcWithdrawRecords")
    public ResponseEntity<?> saveBtcWithdrawRecords(@RequestBody WithdrawalDetailsT withdrawalDetailsT,
                                                    @RequestHeader("Authorization") String authorizationHeader) {
        log.info("WithdrawBtcController, saveBtcWithdrawRecords method start time : {}", Instant.now());
        String jwtToken = authorizationHeader.substring(7);
        String phoneNumber = CommonMethods.getPhoneNumberFromJwt(jwtToken);
        if(phoneNumber.trim().length() == 10){
            phoneNumber += "+91"+phoneNumber;
        }
        log.info("Phone number that we are sending to find in user table is {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber);
        log.info("user response taht we got is {}", user.toString());
        if(user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if(withdrawalDetailsT.getBankAccountId() != null || withdrawalDetailsT.getUpiId() != null) {
            WithdrawalDetailsT withdrawalDetailsT1 = withdrawBtcRepository.save(buildWithdrawDetailsT(withdrawalDetailsT, Long.parseLong(phoneNumber), true));
            log.info("We go the details {}",withdrawalDetailsT1.toString());
            return ResponseEntity.ok().body(withdrawalDetailsT1);
        }
        return ResponseEntity.badRequest().body("There was issue saving the record");
    }

    @GetMapping("/getUserBankDetails")
    public ResponseEntity<?> getUserBankDetails(@RequestParam("phoneNumber") long phoneNumber) {
        log.info("WithdrawBtcController, getUserBankDetails method start time : {}", Instant.now());
        //String phoneNo = CommonMethods.getPhoneNumberFromJwt(phoneNumber);
        String phoneNo = String.valueOf(phoneNumber);
        if(phoneNo.trim().length() == 10){
            phoneNo += "+91"+phoneNo;
        }
            WithdrawalDetailsT withdrawalDetailsT1 = withdrawBtcRepository.getUserBankDetailsByPhoneNumber(Long.parseLong(phoneNo));
            if(withdrawalDetailsT1 != null) {
                if(withdrawalDetailsT1.isActive()) {
                    return ResponseEntity.ok().body(withdrawalDetailsT1);
                } else if (!withdrawalDetailsT1.isActive()) {
                    return ResponseEntity.badRequest().body("Bank details are disabled.");
                }
            }
                return ResponseEntity.badRequest().body("bank account details not available for this number");

    }

    @GetMapping("/getThisUserBankDetails")
    public ResponseEntity<?> getThisUserBankDetails(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("WithdrawBtcController, getThisUserBankDetails method start time : {}", Instant.now());
        String phoneNo = CommonMethods.getPhoneNumberFromJwt(authorizationHeader);
        if(phoneNo.trim().length() == 10){
            phoneNo += "+91"+phoneNo;
        }
        WithdrawalDetailsT withdrawalDetailsT1 = withdrawBtcRepository.getUserBankDetailsByPhoneNumber(Long.parseLong(phoneNo));
        if(withdrawalDetailsT1 != null) {
            if(withdrawalDetailsT1.isActive()) {
                return ResponseEntity.ok().body(withdrawalDetailsT1);
            } else if (!withdrawalDetailsT1.isActive()) {
                return ResponseEntity.badRequest().body("Bank details are disabled.");
            }
        }
        return ResponseEntity.badRequest().body("bank account details not available for this number");

    }

    @PostMapping("/deactivateBankDetails")
    public ResponseEntity<?> updateBankDetailsActiveState(@RequestBody WithdrawalDetailsT withdrawalDetailsT,
                                                          @RequestHeader("Authorization") String authorizationHeader) {

        log.info("WithdrawBtcController, updateBankDetailsActiveState method start time : {}", Instant.now());
        String jwtToken = authorizationHeader.substring(7);
        String phoneNumber = CommonMethods.getPhoneNumberFromJwt(jwtToken);
        if(phoneNumber.trim().length() == 10){
            phoneNumber += "+91"+phoneNumber;
        }

            WithdrawalDetailsT withdrawalDetailsT1 = withdrawBtcRepository.save(buildWithdrawDetailsT(withdrawalDetailsT, Long.parseLong(phoneNumber), false));
            log.info("We go the details {}",withdrawalDetailsT1.toString());
            return ResponseEntity.ok().body(withdrawalDetailsT1);
    }

    private WithdrawalDetailsT buildWithdrawDetailsT(WithdrawalDetailsT withdrawalDetailsT, long phoneNo, boolean isActive) {
        return WithdrawalDetailsT.builder()
                .phoneNumber(phoneNo)
                .createDate(Timestamp.from(Instant.now()))
                .accountHolderName(withdrawalDetailsT.getAccountHolderName())
                .bankAccountId(withdrawalDetailsT.getBankAccountId())
                .ifsc(withdrawalDetailsT.getIfsc())
                .upiId(withdrawalDetailsT.getUpiId())
                .isActive(isActive)
                .build();
    }

}
