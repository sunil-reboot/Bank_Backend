package com.bank.investment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CommonMethods {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

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

    public static String generateRandomAlphanumericString() {
        StringBuilder stringBuilder = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
