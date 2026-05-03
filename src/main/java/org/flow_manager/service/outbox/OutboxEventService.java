package org.flow_manager.service.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flow_manager.dao.OutBoxEventRepository;
import org.flow_manager.model.OutboxEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutBoxEventRepository outBoxEventRepository;
    private final OutboxProcessor outboxProcessor;

    @Transactional
    public void createOutboxEvent(OutboxEvent outboxEvent) {
        outBoxEventRepository.save(outboxEvent);
    }

    @Transactional
    public void processAll() {
        outBoxEventRepository.findAll().forEach(this::process);
    }

    public void process(OutboxEvent outboxEvent) {
        try {
            outboxProcessor.processOutboxEvent(outboxEvent);
            outBoxEventRepository.delete(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
