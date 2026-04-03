package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;   // INCOME or EXPENSE

    @Column(nullable = false)
    private String category;        // e.g. "Salary", "Marketing", "Server"

    @Column(nullable = false)
    private LocalDate date;

    private String notes;

    @Column(nullable = false)
    private boolean deleted = false; // soft delete
}