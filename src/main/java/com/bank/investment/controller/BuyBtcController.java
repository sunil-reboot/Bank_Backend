package com.bank.investment.controller;

import com.bank.investment.model.*;
import com.bank.investment.repository.AvailableBtcQtyRepository;
import com.bank.investment.repository.BtcBuyAndWithdrawDetailsRepository;
import com.bank.investment.repository.BtcConfMRepository;
import com.bank.investment.repository.UserRepository;
import com.bank.investment.service.BtcBuyService;
import com.bank.investment.util.CommonMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BuyBtcController {

    private static final Logger log = LoggerFactory.getLogger(BuyBtcController.class);

    @Autowired
    private BtcBuyAndWithdrawDetailsRepository buyBtcRepository;

    @Autowired
    private BtcConfMRepository btcConfMRepository;

    @Autowired
    private BtcBuyService btcBuyService;


    @Autowired
    private AvailableBtcQtyRepository availableBtcQtyRepository;

    @Autowired
    private UserRepository repository;

    // Customer Data
    @GetMapping("/getBtcTransactionDetails")
    public List<BtcBuyAndWithdrawDetails> getBtcDetails(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("BuyBtcController, getBtcDetails method {}", Instant.now());
        String jwtToken = authorizationHeader.substring(7);
        long phoneNumber = Long.parseLong(CommonMethods.getPhoneNumberFromJwt(jwtToken));
        return buyBtcRepository.getLast10BtcDetails(phoneNumber);
    }

    @PostMapping(value = "/saveBtcRecords")
    public ResponseEntity<?> saveBtcRecords(
            @RequestParam("utrNumber") String utrNumber,
            @RequestParam("quantity") int quantity,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            log.info("We are in saveBtcRecords method {}");

            String jwtToken = authorizationHeader.substring(7);
            String phoneNumber = CommonMethods.getPhoneNumberFromJwt(jwtToken);
            log.info("We are in saveBtcRecords method {}", jwtToken);

            List<BtcBuyAndWithdrawDetails> buyDetailsList = buyBtcRepository.getTodaysList(Long.parseLong(phoneNumber));

//            if(buyDetailsList != null && buyDetailsList.size() >= 2) {
//                log.info("limit crossed");
//                return ResponseEntity.ok().body("Maximum 2 transactions allowed per month");
//            }

            BtcConfM btcConfM = btcConfMRepository.findTopByOrderByCreateDateDesc();
            double btcRate = btcConfM.getBtcRate();

            long totalBtcValue = Math.round(btcRate * quantity);
            BtcBuyAndWithdrawDetails details = BtcBuyAndWithdrawDetails.builder()
                    .btcQty(quantity)
                    .phoneNumber(Long.parseLong(phoneNumber))
                    .UtrNumber(utrNumber)
                    .btcRate(btcConfM.getBtcRate())
                    .btcId(btcConfM.getId())
                    .buyAmount(totalBtcValue)
                    .buyDate(new Timestamp(System.currentTimeMillis()))
                    .type("Buy") // Assuming the type is "Buy"
                    .build();

            BtcBuyAndWithdrawDetails savedDetails = btcBuyService.saveUser(details);

            return ResponseEntity.ok(savedDetails);
        } catch (Exception e) {
            log.error("Unexpected error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @GetMapping("/getAvailableBtcQty")
    public ResponseEntity<?> getAvailableBtcQty(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwtToken = authorizationHeader.substring(7);
            String phoneNumber = CommonMethods.getPhoneNumberFromJwt(jwtToken);
            AvailableBtcQty availableBtcQty = availableBtcQtyRepository.findByPhoneNumber(Long.parseLong(phoneNumber));
            if (availableBtcQty != null) {
                return ResponseEntity.ok(availableBtcQty.getAvailableBalance());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BTC quantity not found for the user.");
            }
        } catch (Exception e) {
            log.error("Error fetching BTC quantity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }


    @GetMapping("/getAvailableBtcQtyForPhoneNumber")
    public ResponseEntity<?> getAvailableBtcQtyForPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        try {
            AvailableBtcQty availableBtcQty = availableBtcQtyRepository.findByPhoneNumber(Long.parseLong(phoneNumber));
            if (availableBtcQty != null) {
                return ResponseEntity.ok(availableBtcQty.getAvailableBalance());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BTC quantity not found for the user.");
            }
        } catch (Exception e) {
            log.error("Error fetching BTC quantity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }


    //cashier Data
    @GetMapping("/getApprovalRecords")
    public List<BtcBuyAndWithdrawDetails> getBtcApprovalRecords() {
        log.info("BuyBtcController, getBtcApprovalRecords method {}", Instant.now());
        return buyBtcRepository.getApprovalDetails();
    }

    // cashier Data
    @PutMapping("/approveOrRejectBtc")
    public ResponseEntity<?> approveOrRejectBtc(@RequestBody Map<String, Object> request,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        log.info("BuyBtcController, approveOrRejectBtc method {}", Instant.now());
        try {
            String jwtToken = authorizationHeader.substring(7);
            long phoneNumber = Long.parseLong(CommonMethods.getPhoneNumberFromJwt(jwtToken));
            BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails =
                    new ObjectMapper().convertValue(request.get("btcBuyAndWithdrawDetails"), BtcBuyAndWithdrawDetails.class);
            boolean isApproved = (boolean) request.get("isApproved");
            boolean isRejected = (boolean) request.get("isRejected");

            String cashierDetails = null; // extract from jwt
            if (btcBuyAndWithdrawDetails != null && btcBuyAndWithdrawDetails.getBtcQty() < 1 && isApproved) {
                return ResponseEntity.badRequest().body("Quantity cannot be less than 1");
            }
            if (btcBuyAndWithdrawDetails != null && btcBuyAndWithdrawDetails.getBtcQty() > 0) {
                // Process the approval or rejection
                btcBuyService.approveOrRejectBtcBuyDetails(phoneNumber, btcBuyAndWithdrawDetails, isApproved, isRejected, cashierDetails);
                return ResponseEntity.ok().body(btcBuyAndWithdrawDetails);
            }

            return ResponseEntity.badRequest().body("Invalid BTC details");
        } catch (Exception e) {
            log.error("Error while approving the records", e);
            return ResponseEntity.badRequest().body("Error while approving the records");
        }
    }


    @GetMapping("/getLatestApprovedOrRejectedRecords")
    public boolean getLatestRejectedOrApprovedRecords(@RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);
        long phoneNumber = Long.parseLong(CommonMethods.getPhoneNumberFromJwt(jwtToken));
        BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails = buyBtcRepository.getLatestApprovedOrRejectedDetails(phoneNumber);
        if(btcBuyAndWithdrawDetails != null && btcBuyAndWithdrawDetails.getUtrNumber() != null) {
            log.info("We are returning rejected details as {}", btcBuyAndWithdrawDetails.isRejected());
            return btcBuyAndWithdrawDetails.isRejected();
        }
        return false;
    }

    @GetMapping("/getPersonName")
    public String getPersonName(@RequestParam final String phoneNumber) {
        String phNo = phoneNumber;
        if(phoneNumber.contains("+")) {
            phNo = phoneNumber;
        } else {
            phNo = "+"+phoneNumber;
        }
        log.info("Phone number that we are passing is {}", phNo);
        User user = repository.findByPhoneNumber(phNo);
        return user.getPersonName();
    }

    private boolean validateBtcQty(BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails) {
        if(btcBuyAndWithdrawDetails.getBtcQty() > 99) {
            return true;
        } else {
            return false;
        }
    }


}
