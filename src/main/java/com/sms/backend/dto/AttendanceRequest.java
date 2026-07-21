package com.sms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceRequest {
    @NotNull(message = "Student is required")
    private Long studentId;

    @NotNull(message = "Class is required")
    private Long classId;

    @NotNull(message = "Date is required")
    private LocalDate attendanceDate;

    @NotBlank(message = "Status is required")
    private String status; // Present | Absent | Late
}
