package org.flow_manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.dao.FileRecordRepository;
import org.flow_manager.exception.MinIOClientException;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.model.FileRecord;
import org.flow_manager.model.OutboxEvent;
import org.flow_manager.model.OutboxType;
import org.flow_manager.model.dto.FileStatus;
import org.flow_manager.model.dto.FlowManagerResponse;
import org.flow_manager.service.outbox.OutboxEventService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FlowManagerService {
    private final MinIOService minioService;
    private final FileRecordRepository fileRecordRepository;
    private final OutboxEventService outboxEventService;
    private final ObjectMapper objectMapper;

    public FlowManagerResponse sendFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String filePath = minioService.getSourceBucket() + "/" + fileName;


        FileRecord fileRecord = fileRecordRepository.save(
                FileRecord.builder()
                    .filePath(filePath)
                    .build()
        );

        try {
            minioService.writeToBucket(fileName, file);
        } catch (Exception e) {
            fileRecord.setStatus(FileStatus.ERROR);
            fileRecordRepository.save(fileRecord);
            log.error(e.getMessage());
            throw new MinIOClientException(e.getMessage());
        }

        fileRecord.setStatus(FileStatus.UPLOADED);
        fileRecordRepository.save(fileRecord);

        outboxEventService.createOutboxEvent(
                OutboxEvent.builder()
                        .outboxType(OutboxType.CONVERTER)
                        .payload(objectMapper.writeValueAsString(
                                new FileConversionEvent(fileRecord.getId(),
                                        fileRecord.getFilePath())
                        ))
                        .build());

        log.info("File uploaded successfully");
        return new FlowManagerResponse(
                fileRecord.getStatus(),
                "File \"" + fileName + "\" has been uploaded successfully.",
                null);
    }

    public FlowManagerResponse getStatus(Long id) {
        FileRecord fileRecord = fileRecordRepository.findById(id)
                .orElseThrow(() -> {
                            log.error("File with id {} not found", id);
                            return new NoSuchElementException();
                        }
                );
        return new FlowManagerResponse(
                fileRecord.getStatus(),
                "Status: " + fileRecord.getFilePath(),
                null);
    }

    public FlowManagerResponse getPdfById(Long id) {
        FileRecord fileRecord = fileRecordRepository.findById(id)
                .orElseThrow(() -> {
                            log.error("File with id {} not found", id);
                            return new NoSuchElementException();
                        }
                );
        if (fileRecord.getStatus() == FileStatus.SUCCESS) {
            String fileName = extractObjectKey(fileRecord.getFilePath());
            try {
                return new FlowManagerResponse(
                        fileRecord.getStatus(),
                        null,
                        minioService.readFromPdfBucket(fileName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return new FlowManagerResponse(
                    fileRecord.getStatus(),
                    "Status: " + fileRecord.getFilePath(),
                    null
            );
        }
    }

    private String extractObjectKey(String minioFullPath) {
        if (minioFullPath == null) return null;
        String trimmed = minioFullPath.trim();
        int slashIndex = trimmed.indexOf('/');

        return (slashIndex == -1) ? trimmed : trimmed.substring(slashIndex + 1);
    }

}
