package com.bank.investment.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RegisterRequest implements Serializable {
    private String phoneNumber;
    private String password;
    private String otp;
    private String name;
    private String email;
}
