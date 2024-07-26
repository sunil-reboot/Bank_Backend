package com.bank.investment.controller;


import com.bank.investment.model.NomineeDetailsT;
import com.bank.investment.model.RegisterRequest;
import com.bank.investment.model.User;
import com.bank.investment.repository.NomineeDetailsRepository;
import com.bank.investment.repository.UserRepository;
import com.bank.investment.service.MailService;
import com.bank.investment.util.CommonMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@RestController
public class SendInviteToNomineeController {

    private static final String NOMINEE_URL = "http://localhost:3000/nomineeSuggestions/";

    @Autowired
    private MailService mailService;

    @Autowired
    private NomineeDetailsRepository nomineeDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/inviteNominee")
    public ResponseEntity<?> sendInviteToNominee(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody final NomineeDetailsT nomineeDetailsT
    ) {
    try {
        String jwtToken = authorizationHeader.substring(7);
        String phoneNumber = CommonMethods.getPhoneNumberFromJwt(jwtToken);
        log.info("We are in saveBtcRecords method {}", jwtToken);
        User userDetails =  userRepository.findByPhoneNumber(phoneNumber);
        NomineeDetailsT checkNomineeDetails = nomineeDetailsRepository.findByEmail(nomineeDetailsT.getEmail());
        if(checkNomineeDetails != null && checkNomineeDetails.getEmail() != null) {
            log.info("Request already sent to email {}", nomineeDetailsT.getEmail());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }

        NomineeDetailsT responseNomineeDetails = nomineeDetailsRepository.save(buildNomineeDetailsT(nomineeDetailsT, userDetails.getPersonName()));
        mailService.sendEmailToNominee(responseNomineeDetails);

        return ResponseEntity.ok(responseNomineeDetails);

    } catch (Exception e) {
        log.error("Unexpected error:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
    }

    }

    private NomineeDetailsT buildNomineeDetailsT(NomineeDetailsT nomineeDetailsT, String personName) {
        return NomineeDetailsT.builder()
                .nomineeName(nomineeDetailsT.getNomineeName())
                .email(nomineeDetailsT.getEmail())
                .relation(nomineeDetailsT.getRelation())
                .userName(personName)
                .createDate(Timestamp.from(Instant.now()))
                .sentDate(Timestamp.from(Instant.now()))
                .url(NOMINEE_URL+CommonMethods.generateRandomAlphanumericString())
                .build();
    }

}
