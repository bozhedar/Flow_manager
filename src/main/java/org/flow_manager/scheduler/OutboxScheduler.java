package org.flow_manager.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.flow_manager.service.outbox.OutboxEventService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxEventService outboxEventService;

    @Scheduled(fixedRate = 2_000)
    @SchedulerLock(
            name = "outbox_processing_task",
            lockAtLeastFor = "2s",
            lockAtMostFor = "PT5M"
    )
    public void run() {
        log.info("Scheduling outbox events ...");
        outboxEventService.processAll();
    }
}
