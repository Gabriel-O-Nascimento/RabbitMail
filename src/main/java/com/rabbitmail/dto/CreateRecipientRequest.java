package com.rabbitmail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRecipientRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must have at most 120 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 180, message = "Email must have at most 180 characters")
    private String email;
}
