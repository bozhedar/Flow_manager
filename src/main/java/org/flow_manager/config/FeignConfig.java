package org.flow_manager.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options options() {
        return new Request.Options(
                5, TimeUnit.SECONDS,
                10, TimeUnit.SECONDS,
                true
        );
    }


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Логирует только метод, URL и статус ответа
    }
}
