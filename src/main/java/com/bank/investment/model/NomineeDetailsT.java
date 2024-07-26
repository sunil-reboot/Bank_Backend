package com.bank.investment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "nominee_details_t")
public class NomineeDetailsT {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    private String nomineeName;
    private String email;
    private String relation;
    private String userName;
    private Timestamp createDate;
    private Timestamp sentDate;
    private String url;

}
