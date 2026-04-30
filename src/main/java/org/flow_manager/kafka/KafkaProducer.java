package org.flow_manager.kafka;

import lombok.RequiredArgsConstructor;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<Long, FileConversionEvent> kafkaTemplate;
    @Value("${kafka.topics.pdf-converter-topic}")
    private String pdfConverterTopic;

    public void sendToPdfConverterTopic(FileConversionEvent event) {
        kafkaTemplate.send(pdfConverterTopic, event);
    }

}