package com.bank.investment.controller;

import com.bank.investment.model.BtcConfM;
import com.bank.investment.repository.BtcConfMRepository;
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
public class ConfigureBtcRateController {

    private static final Logger log = LoggerFactory.getLogger(ConfigureBtcRateController.class);

    @Autowired
    private BtcConfMRepository btcConfMRepository;

    @PostMapping("/configureBtcRate")
    public ResponseEntity<?> configureBtcRate(@RequestBody BtcConfM btcConfM) {
        log.info("ConfigureBtcRateController, configureBtcRate method {}", Instant.now());
        BtcConfM btcConfM1 = btcConfMRepository.save(buildBtcConfM(btcConfM));
        return ResponseEntity.ok().body(btcConfM1);
    }

    @GetMapping("/getCurrentBtcRate")
    public ResponseEntity<BtcConfM> getCurrentBtcRate() {
        BtcConfM currentBtcRate = btcConfMRepository.findTopByOrderByCreateDateDesc();
        return ResponseEntity.ok(currentBtcRate);
    }

    @GetMapping("/getPhoneNumber")
    public long getCurrentPhoneNumber(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("We are in jwt get phone number method");
        String jwtToken = authorizationHeader.substring(7);
        log.info("The return value that we are sending is {}", CommonMethods.getPhoneNumberFromJwt(jwtToken));
        return Long.parseLong(CommonMethods.getPhoneNumberFromJwt(jwtToken));
    }

    private BtcConfM buildBtcConfM(BtcConfM btcConfM) {
        return BtcConfM.builder()
                .btcRate(btcConfM.getBtcRate())
                .createDate(Timestamp.from(Instant.now()))
                .createdBy("ADMIN")
                .build();
    }
}
