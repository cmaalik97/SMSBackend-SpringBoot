package com.sms.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRequest {
    @NotBlank(message = "Class name is required")
    private String className;

    @NotBlank(message = "Section is required")
    @Size(max = 10, message = "Keep this under 10 characters")
    private String section;

    @NotBlank(message = "Room number is required")
    private String roomNo;

    // Optional - class may not have a class teacher yet
    private Long classTeacherId;
}
