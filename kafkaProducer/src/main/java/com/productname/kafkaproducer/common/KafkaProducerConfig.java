package com.productname.kafkaproducer.common;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configurable
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 최적화 추가 설정
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 중복 메시지 전송 방지
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); //모든 복제본 응답 필요
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // 10ms 대기 후 배치 전송
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024); // 32KB 배치 사이즈
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // snappy 압축사용.
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 안정적인 전송을 위한 제한

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
