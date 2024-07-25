package com.bank.investment.controller;

import com.bank.investment.model.Profile;
import com.bank.investment.model.User;
import com.bank.investment.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringEscapeUtils;

@RestController
@Slf4j
public class UpdatePasswordController {

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody Profile profile) {
        String phNo = String.valueOf(profile.getPhoneNumber());
        if (phNo != null && phNo.trim().length() == 12) {
            phNo = "+" + phNo;
        }

        if (profile != null && profile.getPassword() != null && !profile.getPassword().trim().isEmpty()) {
            String sanitizedPassword = StringEscapeUtils.escapeHtml4(profile.getPassword());
            userRepository.updatePasswordForUser(sanitizedPassword, phNo);
            return ResponseEntity.ok("Password updated successfully.");
        }

        return ResponseEntity.badRequest().body("Invalid input.");
    }

    @PostMapping("/checkOldPassword")
    public ResponseEntity<Boolean> checkOldPassword(@RequestBody Profile profile) {
        if (profile != null && profile.getPassword() != null && !profile.getPassword().trim().isEmpty() && profile.getPhoneNumber() > 0) {
            String sanitizedPassword = StringEscapeUtils.escapeHtml4(profile.getPassword());
            User user = userRepository.oldPassword(sanitizedPassword, profile.getPhoneNumber());
            boolean isValid = user != null && user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty();
            return ResponseEntity.ok(isValid);
        }

        return ResponseEntity.badRequest().body(false);
    }
}
