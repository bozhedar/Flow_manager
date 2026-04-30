package org.flow_manager.config;


import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.flow_manager.kafka.event.ErrorEvent;
import org.flow_manager.kafka.event.FileConversionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.pdf-converter-topic}")
    private String nonProcessedPathTopic;
    @Value("${kafka.topics.success-converter-topic}")
    private String processedPathTopic;
    @Value("${kafka.topics.error-converter-topic}")
    private String errorTopic;

    @Value("${kafka.server}")
    private String kafkaServer;

    @Bean
    public NewTopic nonProcessedPathTopic() {
        return new NewTopic(nonProcessedPathTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic pdfProcessedPathTopic() {
        return new NewTopic(processedPathTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic errorTopic() {
        return new NewTopic(errorTopic, 1, (short) 1);
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<Long, FileConversionEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, FileConversionEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Long, ErrorEvent> errorProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, ErrorEvent> errorKafkaTemplate() {
        return new KafkaTemplate<>(errorProducerFactory());
    }
}
