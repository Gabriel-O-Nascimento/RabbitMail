package com.rabbitmail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendEmailRequest {

    @NotBlank(message = "Subject is required")
    @Size(max = 180, message = "Subject must have at most 180 characters")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;
}
