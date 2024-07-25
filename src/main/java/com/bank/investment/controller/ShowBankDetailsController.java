package com.bank.investment.controller;

import com.bank.investment.model.ShowBankDetails;
import com.bank.investment.service.ShowBankDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ShowBankDetailsController {

    @Autowired
    private ShowBankDetailsService showBankDetailsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadUser(@RequestParam("file") MultipartFile multipartFile) {
        try {
            ShowBankDetails showBankDetails = new ShowBankDetails();
            showBankDetails.setDate(Timestamp.from(Instant.now()));
            showBankDetails.setConfiguredBy("ADMIN");
            log.info("showBankDetails {}", showBankDetails);
            ShowBankDetails savedUser = showBankDetailsService.saveUser(showBankDetails, multipartFile);
            return ResponseEntity.ok(savedUser);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
