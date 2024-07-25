package com.bank.investment.controller;

import com.bank.investment.model.BtcBuyAndWithdrawDetails;
import com.bank.investment.repository.BtcBuyAndWithdrawDetailsRepository;
import com.bank.investment.service.BtcBuyService;
import com.bank.investment.util.CommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CashierPaymentController {

    @Autowired
    private BtcBuyAndWithdrawDetailsRepository btcBuyAndWithdrawDetailsRepository;

    @Autowired
    private BtcBuyService btcBuyService;

    @PostMapping("/cashierPayment")
    public ResponseEntity<?> makePaymentToCustomer(@RequestParam("amount") long amount,
                                                   @RequestParam("phoneNumber") long phoneNumber,
                                                   @RequestParam("utrNumber") String utrNumber) {

        if(amount > 0 && phoneNumber > 0) {
            BtcBuyAndWithdrawDetails btcBuyAndWithdrawDetails = btcBuyService.cashierPayment(phoneNumber, amount, utrNumber);
            if(btcBuyAndWithdrawDetails == null) {
                return ResponseEntity.badRequest().body("Incorrect Phone Number/Balance is negative");
            }
            return ResponseEntity.ok().body(btcBuyAndWithdrawDetails);
        }
        return ResponseEntity.badRequest().body("Error while processing your request");

    }
}
