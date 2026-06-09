package org.flow_manager.feign;

import org.flow_manager.config.FeignConfig;
import org.flow_manager.model.dto.SubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "subscribe",
        url = "${subscribe.url}",
        configuration = FeignConfig.class
)
public interface SubscribeClient {
    @GetMapping("api/v1/subscribe")
    SubResponse getSubscribe(@RequestHeader("X-User-Login") String login);
}
