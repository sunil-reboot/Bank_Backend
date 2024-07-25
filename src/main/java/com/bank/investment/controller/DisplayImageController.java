package com.bank.investment.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.bank.investment.model.ShowBankDetails;
import com.bank.investment.service.ShowBankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DisplayImageController {

    @Autowired
    private ShowBankDetailsService showBankDetailsService;

    @GetMapping("/image/{key}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String key) throws IOException {
        return showBankDetailsService.getImage(key);
    }

    @GetMapping("/image/latest")
    public ResponseEntity<String> getLatestImageUrl() {
        String latestImageUrl = showBankDetailsService.getLatestImageUrl();
        if (latestImageUrl != null) {
            return ResponseEntity.ok(latestImageUrl);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
