package org.flow_manager.model.record;

import org.springframework.web.multipart.MultipartFile;

public record FlowManagerResponse(FileStatus status,
                                  String message,
                                  MultipartFile file) {
}
