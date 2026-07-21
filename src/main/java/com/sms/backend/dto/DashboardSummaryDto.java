package com.sms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardSummaryDto {
    private long totalStudents;
    private long totalTeachers;
    private double feesCollected;
    private double feesDue;
    private int attendanceRateToday;
    private long totalClasses;
    private long totalSubjects;
    private long totalResults;
    private long totalUsers;
}
