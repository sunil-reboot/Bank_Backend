package com.bank.investment.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginRequest implements Serializable {
    private String phoneNumber;
    private String password;
}
