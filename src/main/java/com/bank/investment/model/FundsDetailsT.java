package com.bank.investment.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "fund_details_t")
public class FundsDetailsT {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
    private String category; // plan, FTD, MF, Stocks
    private String type; // high, medium, low
    private String riskLevel;
    private double returnOnInvestment; // 18, 20, 15, 6
    private int investmentPeriod;
    private boolean frequentWithDrawl;
    private Timestamp createDate;


}
