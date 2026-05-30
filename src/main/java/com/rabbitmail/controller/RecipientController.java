package com.rabbitmail.controller;

import com.rabbitmail.dto.ApiResponse;
import com.rabbitmail.dto.CreateRecipientRequest;
import com.rabbitmail.entity.Recipient;
import com.rabbitmail.service.RecipientService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientService recipientService;

    @PostMapping
    public ResponseEntity<ApiResponse<Recipient>> create(@Valid @RequestBody CreateRecipientRequest request) {
        Recipient recipient = recipientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Recipient created successfully", recipient));
    }

    @GetMapping
    public ResponseEntity<List<Recipient>> findAll() {
        return ResponseEntity.ok(recipientService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Recipient>> findActive() {
        return ResponseEntity.ok(recipientService.findActive());
    }
}
