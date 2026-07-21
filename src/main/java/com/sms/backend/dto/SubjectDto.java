package com.sms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long classId;
    private String className;
    private Long teacherId;
    private String teacherName;
}
