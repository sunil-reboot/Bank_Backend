package com.bank.investment.controller;

import com.bank.investment.model.AvailableBtcQty;
import com.bank.investment.model.MakePaymentUsers;
import com.bank.investment.model.WithdrawalDetailsT;
import com.bank.investment.repository.FindUnApprovedUsersRepository;
import com.bank.investment.repository.UserRepository;
import com.bank.investment.model.User;
import com.bank.investment.repository.WithdrawBtcRepository;
import com.bank.investment.util.CommonMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DisplayAllUnApprovedUsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FindUnApprovedUsersRepository findUnApprovedUsersRepository;

    @Autowired
    private WithdrawBtcRepository withdrawBtcRepository;

    @GetMapping("/unApprovedUsers")
    public ResponseEntity<?> getAllUnapprovedUsers(@RequestHeader("Authorization") String authorizationHeader ) {
        String jwtToken = authorizationHeader.substring(7);
        long phoneNumber = Long.parseLong(CommonMethods.getPhoneNumberFromJwt(jwtToken));
        String phNo = String.valueOf(phoneNumber);
        if(phNo.trim().length() == 10) {
            phNo = "+91"+phNo;
        } else {
            phNo = "+"+phNo;
        }
        User user = userRepository.findByPhoneNumber(phNo);
        if(user != null && (user.getRoleName().equalsIgnoreCase("CASHIER") || user.getRoleName().equalsIgnoreCase("ADMIN"))) {
            WithdrawalDetailsT withdrawalDetailsT = withdrawBtcRepository.getUserBankDetailsByPhoneNumber(Long.parseLong(phNo));
            
            List<AvailableBtcQty> displayUnapprovedNumbersList = findUnApprovedUsersRepository.getAllUnapprovedUsers();
            if(displayUnapprovedNumbersList.isEmpty()) {
                return ResponseEntity.ok().body("No users left");
            }
            return ResponseEntity.ok().body(displayUnapprovedNumbersList);
        } else {
            return ResponseEntity.ok().body("User is not cashier");
        }

    }

}
