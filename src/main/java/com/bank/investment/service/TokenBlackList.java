package com.bank.investment.service;

public interface TokenBlackList {
    void addToBlacklist(String token);

    boolean isBlacklisted(String token);
}
