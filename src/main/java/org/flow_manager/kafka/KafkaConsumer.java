package org.flow_manager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.dao.FileRecordRepository;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.model.FileRecord;
import org.flow_manager.model.dto.FileStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final FileRecordRepository fileRecordRepository;

    @KafkaListener(topics = "${kafka.topics.success-converter-topic}", groupId = "${kafka.group_id}")
    public void consumeProcessedPath(FileConversionEvent event) {
        log.debug("Received processed path event: {}", event.id());
        FileRecord fileRecord = fileRecordRepository.findById(event.id())
                .orElseThrow(() -> {
                    log.error("FileRecord not found with id {}", event.id());
                    return new NoSuchElementException("FileRecord not found with id " + event.id());}
                );
        fileRecord.setStatus(FileStatus.SUCCESS);
        fileRecord.setFilePath(event.filePath());
        fileRecordRepository.save(fileRecord);
    }

    @KafkaListener(topics = "${kafka.topics.error-converter-topic}", groupId = "${kafka.group_id}")
    public void consumeErrorPath(ErrorEvent event) {
        log.debug("Received error event: {}", event.id());
        FileRecord fileRecord = fileRecordRepository.findById(event.id())
                .orElseThrow(() -> {
                    log.error("FileRecord not found with id {}", event.id());
                    return new NoSuchElementException("FileRecord not found with id " + event.id());}
                );
        fileRecord.setStatus(FileStatus.ERROR);
        fileRecordRepository.save(fileRecord);
    }
}