package com.bank.investment.model;

import lombok.Data;

@Data
public class MakePaymentUsers {

    private long phoneNumber;
    private long totalBuyAmount;
    private long availableBalance;
}
