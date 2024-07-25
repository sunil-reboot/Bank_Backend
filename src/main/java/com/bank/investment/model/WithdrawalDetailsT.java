package com.bank.investment.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name= "Withdrawal_Details_T")
public class WithdrawalDetailsT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "phone_number")
    private long phoneNumber;
    @Column(name = "bank_account_id")
    private String bankAccountId;
    @Column(name = "ifsc")
    private String ifsc;
    @Column(name = "account_holder_name")
    private String accountHolderName;
    @Column(name = "upi_id")
    private String upiId;
    @Column(name = "create_date")
    private Timestamp createDate;
    @Column(name= "is_active")
    private boolean isActive;

}
