package com.bank.investment.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class InMemoryTokenBlacklist implements TokenBlackList {

    private static final  Integer EXPIRE_MIN = 60;
    private final LoadingCache<String,Boolean> blacklist;

    public InMemoryTokenBlacklist() {
        blacklist = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Boolean load(final String s) {
                        return false;
                    }
                });
    }

    @Override
    public void addToBlacklist(final String token) {
        blacklist.put(token, true);
    }

    @Override
    public boolean isBlacklisted(final String token) {
        try {
            return blacklist.get(token);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

