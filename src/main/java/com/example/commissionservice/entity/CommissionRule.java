package com.example.commissionservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class CommissionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String counterPartyId;
    String product;
    String exchange;
    Long rulePrecedence;
    String tradeValue;
    @NonNull
    Double computationFactor; //for demonstration . Change to expressions as required


}
