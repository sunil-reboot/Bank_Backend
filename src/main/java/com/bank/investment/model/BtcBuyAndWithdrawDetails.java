package com.bank.investment.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "btc_buy_withdraw_details")
public class BtcBuyAndWithdrawDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long phoneNumber;
    private int btcQty;
    private long buyAmount;
    private long withdrawAmount;
    private String withdrawPersonDetails;
    private Timestamp buyDate;
    private double btcRate;
    private long btcId;
    private Timestamp approvalDate;

    @JsonProperty("isApproved")
    private boolean isApproved;

    @JsonProperty("isRejected")
    private boolean isRejected;

    private Timestamp rejectedDate;
    private String cashierDetails;
    //private String s3Url;

    @Column(name = "utr_number", unique = true)
    private String UtrNumber;
    private String type; // Buy or Withdraw
    @Column(name = "bank_details_id")
    private long bankDetailsId;
}
