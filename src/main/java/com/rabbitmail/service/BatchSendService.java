package com.rabbitmail.service;

import com.rabbitmail.entity.BatchSend;
import com.rabbitmail.entity.BatchSendResult;
import com.rabbitmail.repository.BatchSendRepository;
import com.rabbitmail.repository.BatchSendResultRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BatchSendService {

    private final BatchSendRepository batchSendRepository;
    private final BatchSendResultRepository batchSendResultRepository;

    @Transactional(readOnly = true)
    public List<BatchSend> findAll() {
        return batchSendRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BatchSendResult> findResultsByBatchSendId(Long batchSendId) {
        if (!batchSendRepository.existsById(batchSendId)) {
            throw new IllegalArgumentException("Batch send not found");
        }

        return batchSendResultRepository.findByBatchSendIdOrderByIdAsc(batchSendId);
    }
}
