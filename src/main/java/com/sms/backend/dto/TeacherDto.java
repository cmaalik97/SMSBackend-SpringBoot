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
public class TeacherDto {
    private Long teacherId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String qualification;
    private Double salary;
    private LocalDate joiningDate;
    private String address;
}
