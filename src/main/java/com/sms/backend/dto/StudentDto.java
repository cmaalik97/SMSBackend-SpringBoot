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
public class StudentDto {
    private Long studentId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private Long classId;
    private String className;
    private String parentName;
    private String parentPhone;
    private String address;
}
