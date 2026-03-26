package com.charter.rewardsapplication.model;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTransaction {

    @Id
    private Long id;

    private Long customerId;

    private String customerName;

    private Double amount;

    private LocalDate transactionDate;
}
