package com.rabbitmail.controller;

import com.rabbitmail.dto.ApiResponse;
import com.rabbitmail.dto.CreateEmailMessageRequest;
import com.rabbitmail.dto.SendEmailRequest;
import com.rabbitmail.entity.BatchSend;
import com.rabbitmail.entity.EmailMessage;
import com.rabbitmail.service.EmailMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class EmailMessageController {

    private final EmailMessageService emailMessageService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmailMessage>> create(@Valid @RequestBody CreateEmailMessageRequest request) {
        EmailMessage emailMessage = emailMessageService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Email message created as draft", emailMessage));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<BatchSend>> requestSend(@Valid @RequestBody SendEmailRequest request) {
        BatchSend batchSend = emailMessageService.requestSend(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>("Email batch send requested successfully", batchSend));
    }
}
