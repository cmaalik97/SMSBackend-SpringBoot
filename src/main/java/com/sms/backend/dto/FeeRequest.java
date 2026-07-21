package com.sms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FeeRequest {
    @NotNull(message = "Student is required")
    private Long studentId;

    @NotBlank(message = "Fee type is required")
    private String feeType;

    @NotNull(message = "Amount due is required")
    @PositiveOrZero(message = "Amount due can't be negative")
    private Double amountDue;

    @NotNull(message = "Amount paid is required")
    @PositiveOrZero(message = "Amount paid can't be negative")
    private Double amountPaid;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
}
