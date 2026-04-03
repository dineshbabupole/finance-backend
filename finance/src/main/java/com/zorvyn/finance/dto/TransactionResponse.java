package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String notes;
}