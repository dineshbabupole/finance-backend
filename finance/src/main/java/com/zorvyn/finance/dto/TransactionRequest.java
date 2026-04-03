package com.zorvyn.finance.dto;

import com.zorvyn.finance.model.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String notes;
}