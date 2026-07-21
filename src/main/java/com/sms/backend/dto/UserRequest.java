package com.sms.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Request body for POST/PUT /api/users
@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    // Only required when creating; on update an empty password means "keep existing"
    private String password;

    @NotBlank(message = "Role is required")
    private String role; // "Admin" | "Teacher" | "Student"
}
