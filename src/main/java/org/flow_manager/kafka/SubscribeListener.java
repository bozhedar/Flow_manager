package org.flow_manager.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.kafka.event.SubscribeEvent;
import org.flow_manager.service.SubscribeCacheService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscribeListener {
    private final SubscribeCacheService subscribeCacheService;

    @KafkaListener(topics = "${kafka.topics.subscribe-expired-topic}", groupId = "${kafka.group_id}")
    public void consumeExpiredSubTopic(SubscribeEvent event) {
        log.debug("Received subscribe-expired event for {}", event.login());
        subscribeCacheService.clearSubscriptionCache(event.login());
    }

    @KafkaListener(topics = "${kafka.topics.subscribe-paid-topic}", groupId = "${kafka.group_id}")
    public void consumePaidSubTopic(SubscribeEvent event) {
        log.debug("Received subscribe-paid event for {}", event.login());
        subscribeCacheService.clearSubscriptionCache(event.login());
    }
}
