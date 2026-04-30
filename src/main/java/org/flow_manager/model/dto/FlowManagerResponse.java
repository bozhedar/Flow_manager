package org.flow_manager.model.dto;

import java.io.InputStream;

public record FlowManagerResponse(FileStatus status,
                                  String message,
                                  InputStream file) {
}
