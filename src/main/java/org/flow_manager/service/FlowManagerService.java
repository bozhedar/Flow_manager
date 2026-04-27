package org.flow_manager.service;

import lombok.RequiredArgsConstructor;
import org.flow_manager.model.record.FlowManagerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FlowManagerService {
    private final MinIOService minioService;

    public FlowManagerResponse sendFile(MultipartFile file) {
    }

    public FlowManagerResponse getStatus(Long id) {
    }

    public FlowManagerResponse getPdfById(Long id) {
    }

    /*
    TODO
    kafka(config, producer)
    filepath db
    */

}
