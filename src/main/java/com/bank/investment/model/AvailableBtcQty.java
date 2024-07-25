package com.bank.investment.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "available_btc_qty")
public class AvailableBtcQty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private long phoneNumber;
    private Timestamp createDate;
    private Timestamp updatedDate;
    private int btcQty;
    private long availableBalance;

}
