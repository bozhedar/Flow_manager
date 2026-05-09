package org.flow_manager.util;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.dao.FileRecordRepository;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.model.FileRecord;
import org.flow_manager.model.OutboxEvent;
import org.flow_manager.model.OutboxType;
import org.flow_manager.service.outbox.OutboxEventService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalUtil {
    private final FileRecordRepository fileRecordRepository;
    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;

    public FileRecord saveFileRecord(String filePath) {

        FileRecord fileRecord = fileRecordRepository.save(
                FileRecord.builder()
                        .filePath(filePath)
                        .build());

        outboxEventService.createOutboxEvent(
                OutboxEvent.builder()
                        .outboxType(OutboxType.CONVERTER)
                        .payload(objectMapper.writeValueAsString(
                                new FileConversionEvent(fileRecord.getId(),
                                        fileRecord.getFilePath())
                        ))
                        .build());

        return fileRecord;
    }
}
