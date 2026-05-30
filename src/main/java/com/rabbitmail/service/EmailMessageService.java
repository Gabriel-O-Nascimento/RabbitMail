package com.rabbitmail.service;

import com.rabbitmail.dto.CreateEmailMessageRequest;
import com.rabbitmail.dto.EmailQueueMessage;
import com.rabbitmail.dto.SendEmailRequest;
import com.rabbitmail.entity.BatchSend;
import com.rabbitmail.entity.EmailMessage;
import com.rabbitmail.enums.BatchSendStatus;
import com.rabbitmail.enums.EmailMessageStatus;
import com.rabbitmail.producer.EmailProducer;
import com.rabbitmail.repository.BatchSendRepository;
import com.rabbitmail.repository.EmailMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailMessageService {

    private final EmailMessageRepository emailMessageRepository;
    private final BatchSendRepository batchSendRepository;
    private final EmailProducer emailProducer;

    @Transactional
    public EmailMessage createDraft(CreateEmailMessageRequest request) {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(request.getSubject())
                .content(request.getContent())
                .status(EmailMessageStatus.DRAFT)
                .build();

        return emailMessageRepository.save(emailMessage);
    }

    @Transactional
    public BatchSend requestSend(SendEmailRequest request) {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(request.getSubject())
                .content(request.getContent())
                .status(EmailMessageStatus.QUEUED)
                .build();
        EmailMessage savedMessage = emailMessageRepository.save(emailMessage);

        BatchSend batchSend = BatchSend.builder()
                .emailMessage(savedMessage)
                .status(BatchSendStatus.REQUESTED)
                .totalRecipients(0)
                .totalSuccess(0)
                .totalError(0)
                .build();
        BatchSend savedBatchSend = batchSendRepository.save(batchSend);

        EmailQueueMessage queueMessage = new EmailQueueMessage(savedMessage.getId(), savedBatchSend.getId());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailProducer.publishEmailRequest(queueMessage);
            }
        });

        return savedBatchSend;
    }
}
