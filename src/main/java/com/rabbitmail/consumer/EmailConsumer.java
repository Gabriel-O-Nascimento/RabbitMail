package com.rabbitmail.consumer;

import com.rabbitmail.dto.EmailQueueMessage;
import com.rabbitmail.entity.BatchSend;
import com.rabbitmail.entity.BatchSendResult;
import com.rabbitmail.entity.EmailMessage;
import com.rabbitmail.entity.Recipient;
import com.rabbitmail.enums.BatchSendResultStatus;
import com.rabbitmail.enums.BatchSendStatus;
import com.rabbitmail.enums.EmailMessageStatus;
import com.rabbitmail.repository.BatchSendRepository;
import com.rabbitmail.repository.BatchSendResultRepository;
import com.rabbitmail.repository.EmailMessageRepository;
import com.rabbitmail.repository.RecipientRepository;
import com.rabbitmail.service.EmailSenderService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final BatchSendRepository batchSendRepository;
    private final BatchSendResultRepository batchSendResultRepository;
    private final EmailMessageRepository emailMessageRepository;
    private final RecipientRepository recipientRepository;
    private final EmailSenderService emailSenderService;

    @Transactional
    @RabbitListener(queues = "${rabbitmail.rabbitmq.queue}")
    public void consume(EmailQueueMessage queueMessage) {
        log.info("Email request consumed. messageId={}, batchSendId={}",
                queueMessage.getMessageId(), queueMessage.getBatchSendId());

        BatchSend batchSend = batchSendRepository.findById(queueMessage.getBatchSendId())
                .orElseThrow(() -> new IllegalArgumentException("Batch send not found"));

        EmailMessage emailMessage = emailMessageRepository.findById(queueMessage.getMessageId())
                .orElseThrow(() -> new IllegalArgumentException("Email message not found"));

        batchSend.setStatus(BatchSendStatus.PROCESSING);
        emailMessage.setStatus(EmailMessageStatus.SENDING);
        batchSendRepository.save(batchSend);
        emailMessageRepository.save(emailMessage);

        List<Recipient> activeRecipients = recipientRepository.findByActiveTrueOrderByNameAsc();
        int totalSuccess = 0;
        int totalError = 0;

        log.info("Processing batchSendId={} for {} active recipients", batchSend.getId(), activeRecipients.size());

        for (Recipient recipient : activeRecipients) {
            try {
                emailSenderService.send(recipient, emailMessage);
                saveResult(batchSend, recipient, BatchSendResultStatus.SUCCESS, null);
                totalSuccess++;
            } catch (Exception exception) {
                log.error("Email send failed to {}. Error: {}", recipient.getEmail(), exception.getMessage());
                saveResult(batchSend, recipient, BatchSendResultStatus.ERROR, exception.getMessage());
                totalError++;
            }
        }

        finishBatch(batchSend, emailMessage, activeRecipients.size(), totalSuccess, totalError);
        log.info("Batch processing finished. batchSendId={}, success={}, error={}",
                batchSend.getId(), totalSuccess, totalError);
    }

    private void saveResult(
            BatchSend batchSend,
            Recipient recipient,
            BatchSendResultStatus status,
            String errorMessage
    ) {
        BatchSendResult result = BatchSendResult.builder()
                .batchSend(batchSend)
                .recipient(recipient)
                .emailUsed(recipient.getEmail())
                .status(status)
                .errorMessage(errorMessage)
                .sentAt(LocalDateTime.now())
                .build();

        batchSendResultRepository.save(result);
    }

    private void finishBatch(
            BatchSend batchSend,
            EmailMessage emailMessage,
            int totalRecipients,
            int totalSuccess,
            int totalError
    ) {
        batchSend.setTotalRecipients(totalRecipients);
        batchSend.setTotalSuccess(totalSuccess);
        batchSend.setTotalError(totalError);
        batchSend.setProcessedAt(LocalDateTime.now());

        if (totalRecipients == 0 || totalSuccess == 0) {
            batchSend.setStatus(BatchSendStatus.ERROR);
            emailMessage.setStatus(EmailMessageStatus.ERROR);
        } else if (totalError > 0) {
            batchSend.setStatus(BatchSendStatus.COMPLETED_WITH_ERRORS);
            emailMessage.setStatus(EmailMessageStatus.ERROR);
        } else {
            batchSend.setStatus(BatchSendStatus.COMPLETED);
            emailMessage.setStatus(EmailMessageStatus.SENT);
        }

        batchSendRepository.save(batchSend);
        emailMessageRepository.save(emailMessage);
    }
}
