package org.flow_manager.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.kafka.ConverterListener;
import org.flow_manager.kafka.FlowManagerProducer;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.flow_manager.model.OutboxEvent;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConverterOutboxProcessor implements OutboxProcessor{
    private final FlowManagerProducer producer;
    private final ObjectMapper objectMapper;

    @Override
    public void processOutboxEvent(OutboxEvent outboxEvent) {
        FileConversionEvent event = objectMapper.readValue(outboxEvent.getPayload(), FileConversionEvent.class);
        producer.sendToPdfConverterTopic(event);
    }
}
