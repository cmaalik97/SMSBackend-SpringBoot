package com.sms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeDto {
    private Long feeId;
    private Long studentId;
    private String studentName;
    private String feeType;
    private Double amountDue;
    private Double amountPaid;
    private LocalDate dueDate;
    private String status;
}
