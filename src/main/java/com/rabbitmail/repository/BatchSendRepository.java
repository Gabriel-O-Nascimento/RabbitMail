package com.rabbitmail.repository;

import com.rabbitmail.entity.BatchSend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchSendRepository extends JpaRepository<BatchSend, Long> {
}
