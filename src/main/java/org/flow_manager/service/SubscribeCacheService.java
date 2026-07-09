package org.flow_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flow_manager.feign.SubscribeClient;
import org.flow_manager.model.dto.SubResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeCacheService {
    private final SubscribeClient subscribeClient;

    @Cacheable(value = "subs", key = "#login")
    public SubResponse getSubscriptionStatus(String login) {
        log.info("Cache is missing for {}", login);
        return subscribeClient.getSubscribe(login);
    }

    @CacheEvict(value = "subs", key = "#login")
    public void clearSubscriptionCache(String login) {
        log.info("Cache is cleared for {}", login);
    }

}
