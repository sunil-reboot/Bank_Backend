package com.bank.investment.model;


import java.io.Serializable;

import lombok.Data;

@Data
public class ChangePasswordRequest implements Serializable {
    private String phoneNumber;
    private String newPassword;
    private String otp;
}
