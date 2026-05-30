package com.rabbitmail.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailQueueMessage implements Serializable {

    private Long messageId;
    private Long batchSendId;
}
