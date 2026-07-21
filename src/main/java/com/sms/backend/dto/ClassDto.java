package com.sms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto {
    private Long classId;
    private String className;
    private String section;
    private String roomNo;
    private Long classTeacherId;
    private String classTeacherName;
}
