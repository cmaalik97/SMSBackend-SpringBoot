package com.sms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResultRequest {
    @NotNull(message = "Student is required")
    private Long studentId;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    @NotBlank(message = "Exam type is required")
    private String examType; // Quiz | Midterm | Final

    @NotNull(message = "Marks are required")
    @PositiveOrZero(message = "Marks can't be negative")
    private Double marks;

    @NotNull(message = "Exam date is required")
    private LocalDate examDate;
}
