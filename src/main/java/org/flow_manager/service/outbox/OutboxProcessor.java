package org.flow_manager.service.outbox;

import org.flow_manager.model.OutboxEvent;

public interface OutboxProcessor {
    void processOutboxEvent(OutboxEvent outboxEvent);
}
