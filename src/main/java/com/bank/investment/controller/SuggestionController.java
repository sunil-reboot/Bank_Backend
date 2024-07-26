package com.bank.investment.controller;

import com.bank.investment.model.FundsDetailsT;
import com.bank.investment.model.NominatedDetailsT;
import com.bank.investment.model.NomineeDetailsT;
import com.bank.investment.repository.FundDetailsRepository;
import com.bank.investment.repository.NominatedFundDetailsRepository;
import com.bank.investment.repository.NomineeDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@Slf4j
public class SuggestionController {

    @Value("classpath:static/Suggestions.json")
    private Resource suggestionsResource;

    @Autowired
    private FundDetailsRepository fundDetailsRepository;

    @Autowired
    private NomineeDetailsRepository nomineeDetailsRepository;

    @Autowired
    private NominatedFundDetailsRepository nominatedFundDetailsRepository;

    private static final String USER_URL="http://localhost:3000/nomineeSuggestions/";

    @GetMapping("/api/client/auth/nomineeSuggestions/{id}")
    public ResponseEntity<String> getNomineeSuggestions(@PathVariable String id) {
        log.info("Received ID in nomineeSuggestions: {}", id);

        try {
            String suggestionsJson = new String(Files.readAllBytes(Paths.get(suggestionsResource.getURI())));
            log.info("String that we are returning is  {}", suggestionsJson);
            return ResponseEntity.ok(suggestionsJson);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load suggestions");
        }
    }

    @GetMapping("/api/client/auth/generalSuggestions/{id}")
    public ResponseEntity<?> getGeneralSuggestions(@PathVariable String id) {
        log.info("Received ID in nomineeSuggestions:  getGeneralSuggestions {}", id);

        try {
            List<FundsDetailsT> funds = fundDetailsRepository.findAll();
            return ResponseEntity.ok(funds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load suggestions");
        }
    }

    @GetMapping("/api/client/auth/generalSuggestions")
    public ResponseEntity<?> getAllSuggestions() {
        log.info("Received ID in nomineeSuggestions:  getGeneralSuggestions {}");

        try {
            List<FundsDetailsT> funds = fundDetailsRepository.findAll();
            return ResponseEntity.ok(funds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load suggestions");
        }
    }

    @PostMapping("/api/client/auth/suggestedFund/{id}")
    public ResponseEntity<?> getSuggestedFund(@PathVariable String id,
                                              @RequestBody FundsDetailsT fundsDetailsT) {
        log.info("Received ID in getSuggestedFund getSuggestedFund: {}", id);

        try {
            String url = USER_URL+id;
            log.info("Received URL in nomineeSuggestions getSuggestedFund: {}", url);
            NomineeDetailsT nomineeDetailsT = nomineeDetailsRepository.findByUrl(url);
            if(nomineeDetailsT == null || nomineeDetailsT.getUrl() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid URL");
            }
            log.info("Found nomineeDetailsT");
            FundsDetailsT fund = fundDetailsRepository.findByName(fundsDetailsT.getName());

            log.info("Found fund ");
            NominatedDetailsT nominatedDetailsT = nominatedFundDetailsRepository.save(buildNominateFundDetails(fund, nomineeDetailsT));

            log.info("Nominated details saved nominatedDetailsT");
            return ResponseEntity.ok(nominatedDetailsT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load suggestions");
        }
    }

    @PostMapping("/api/client/auth/saveFunds")
    public ResponseEntity<?> saveFunds(@RequestBody FundsDetailsT fundsDetailsT) {
        log.info("saving funds in database");
        try {
            fundDetailsRepository.save(buildFundsDetailsT(fundsDetailsT));
            return ResponseEntity.ok(fundsDetailsT);
        } catch (Exception e) {
            log.error("Exception in saving funds {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    private NominatedDetailsT buildNominateFundDetails(FundsDetailsT fundsDetailsT, NomineeDetailsT nomineeDetailsT) {
        return NominatedDetailsT.builder()
                .fundName(fundsDetailsT.getName())
                .createDate(Timestamp.from(Instant.now()))
                .url(nomineeDetailsT.getUrl())
                .userName(nomineeDetailsT.getUserName())
                .nomineeName(nomineeDetailsT.getNomineeName())
                .build();
    }

    private FundsDetailsT buildFundsDetailsT(FundsDetailsT fundsDetailsT) {
        return FundsDetailsT.builder()
                .createDate(Timestamp.from(Instant.now()))
                .type(fundsDetailsT.getType())
                .category(fundsDetailsT.getCategory())
                .frequentWithDrawl(fundsDetailsT.isFrequentWithDrawl())
                .investmentPeriod(fundsDetailsT.getInvestmentPeriod())
                .returnOnInvestment(fundsDetailsT.getReturnOnInvestment())
                .name(fundsDetailsT.getName())
                .build();

    }
}
