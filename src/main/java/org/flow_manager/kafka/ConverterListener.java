package org.flow_manager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.service.FileStatusService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConverterListener {
    private final FileStatusService fileStatusService;

    @KafkaListener(topics = "${kafka.topics.success-converter-topic}", groupId = "${kafka.group_id}")
    public void consumeProcessedPath(FileConversionEvent event) {
        log.debug("Received processed path event: {}", event.id());
        fileStatusService.handleConvertedFile(event);
    }

    @KafkaListener(topics = "${kafka.topics.error-converter-topic}", groupId = "${kafka.group_id}")
    public void consumeErrorEvent(ErrorEvent event) {
        log.debug("Received error event: {}", event.id());
        fileStatusService.handleErrorEvent(event);
    }
}