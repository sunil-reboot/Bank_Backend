package com.bank.investment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class CommonMethods {

    public static String getPhoneNumberFromJwt(String jwtToken) {
        log.info("jwtToken {}", jwtToken);
        try {
            // Split the JWT token into header, payload, and signature
            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }

            // Decode the payload
            String payload = new String(Base64.getDecoder().decode(parts[1]));

            // Parse the payload JSON to extract claims
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

            // Extract the "sub" value (assuming phone number is stored in "sub")
            return claims.get("sub").toString();
        } catch (IllegalArgumentException | IOException e) {
            log.error("Error parsing JWT token", e);
            return null;
        }
    }
}
