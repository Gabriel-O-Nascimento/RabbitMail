package com.rabbitmail.repository;

import com.rabbitmail.entity.BatchSendResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchSendResultRepository extends JpaRepository<BatchSendResult, Long> {

    List<BatchSendResult> findByBatchSendIdOrderByIdAsc(Long batchSendId);
}
