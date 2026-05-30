package com.rabbitmail.controller;

import com.rabbitmail.entity.BatchSend;
import com.rabbitmail.entity.BatchSendResult;
import com.rabbitmail.service.BatchSendService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch-sends")
@RequiredArgsConstructor
public class BatchSendController {

    private final BatchSendService batchSendService;

    @GetMapping
    public ResponseEntity<List<BatchSend>> findAll() {
        return ResponseEntity.ok(batchSendService.findAll());
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<BatchSendResult>> findResults(@PathVariable Long id) {
        return ResponseEntity.ok(batchSendService.findResultsByBatchSendId(id));
    }
}
