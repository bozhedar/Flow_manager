package org.flow_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.dao.FileRecordRepository;
import org.flow_manager.exception.FileSizeException;
import org.flow_manager.exception.MinIOClientException;
import org.flow_manager.feign.SubscribeClient;
import org.flow_manager.model.FileRecord;
import org.flow_manager.model.dto.FileStatus;
import org.flow_manager.model.dto.FlowManagerResponse;
import org.flow_manager.util.TransactionalUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowManagerService {
    private final MinIOService minioService;
    private final FileRecordRepository fileRecordRepository;
    private final TransactionalUtil transactionalUtil;
    private final SubscribeCacheService subscribeCacheService;

    @Value("${file-size.paid}")
    private long PAID_FILE_SIZE;
    @Value("${file-size.free}")
    private long FREE_FILE_SIZE;

    public FlowManagerResponse uploadFile(MultipartFile file, String login) {
        checkSubscribe(file, login);

        String fileName = file.getOriginalFilename();
        String filePath = minioService.getSourceBucket() + "/" + fileName;

        FileRecord fileRecord = transactionalUtil.saveFileRecord(filePath);

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

    private void checkSubscribe(MultipartFile file, String login) {
        long fileSizeMB =  file.getSize() / (1024^2);

        if (subscribeCacheService
                .getSubscriptionStatus(login)
                .isPaidSubscribe()) {
            if (fileSizeMB > PAID_FILE_SIZE) {
                throw new FileSizeException("File size must be less than 100MB");
            }

        } else  {
            if (fileSizeMB > FREE_FILE_SIZE) {
                throw new FileSizeException("File size must be less than 50MB");
            }
        }
    }
}
