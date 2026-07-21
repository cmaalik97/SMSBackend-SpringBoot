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
public class ResultDto {
    private Long resultId;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private String examType;
    private Double marks;
    private Double maxMarks;
    private String grade;
    private LocalDate examDate;
}
