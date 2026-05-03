package org.flow_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.dao.FileRecordRepository;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.model.FileRecord;
import org.flow_manager.model.dto.FileStatus;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStatusService {
    private final FileRecordRepository fileRecordRepository;

    public void handleConvertedFile(FileConversionEvent event) {
        FileRecord fileRecord = fileRecordRepository.findById(event.id())
                .orElseThrow(() -> {
                    log.error("FileRecord not found with id {}", event.id());
                    return new NoSuchElementException("FileRecord not found with id " + event.id());}
                );
        fileRecord.setStatus(FileStatus.SUCCESS);
        fileRecord.setFilePath(event.filePath());
        fileRecordRepository.save(fileRecord);
    }

    public void handleErrorEvent(ErrorEvent event) {
        FileRecord fileRecord = fileRecordRepository.findById(event.id())
                .orElseThrow(() -> {
                    log.error("FileRecord not found with id {}", event.id());
                    return new NoSuchElementException("FileRecord not found with id " + event.id());}
                );
        fileRecord.setStatus(FileStatus.ERROR);
        fileRecordRepository.save(fileRecord);
    }
}
