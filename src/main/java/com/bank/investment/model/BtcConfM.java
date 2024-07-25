package com.bank.investment.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "btc_conf_m")
public class BtcConfM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double btcRate;
    private Timestamp createDate;
    private String createdBy;
}
