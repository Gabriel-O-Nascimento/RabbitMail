package com.rabbitmail.service;

import com.rabbitmail.dto.CreateRecipientRequest;
import com.rabbitmail.entity.Recipient;
import com.rabbitmail.repository.RecipientRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;

    @Transactional
    public Recipient create(CreateRecipientRequest request) {
        if (recipientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Recipient email already exists");
        }

        Recipient recipient = Recipient.builder()
                .name(request.getName())
                .email(request.getEmail())
                .active(true)
                .build();

        return recipientRepository.save(recipient);
    }

    @Transactional(readOnly = true)
    public List<Recipient> findAll() {
        return recipientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Recipient> findActive() {
        return recipientRepository.findByActiveTrueOrderByNameAsc();
    }
}
