package com.productname.kafkaconsumer.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String BOOTSTRAP_SERVERS;
	private final static String GROUP_ID = "product.name.auto.v1";

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		// 자동 커밋 비활성화: 수동 커밋을 위해 false로 설정합니다.
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		// 한 번의 poll()에서 최대 1000개의 레코드를 가져옵니다.
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

		// 브로커와 컨슈머 시간 설정.
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
		props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);

		// 최적화 추가 설정
		props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // 시작 오프셋 설정
		props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
				"org.apache.kafka.clients.consumer.CooperativeStickyAssignor");

		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		// 수동 커밋 모드 (처리 완료 후 ack.acknowledge() 호출하면 커밋)
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		// 리밸런스 이벤트 처리 리스너 등록
		factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
			@Override
			public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
				// 리밸런스 전에 커밋되지 않은 오프셋을 안전하게 커밋할 수 있음
				if (!partitions.isEmpty()) {
					consumer.commitSync();
					log.warn("Partitions revoked, comitted offsets for: " + partitions);
				}
			}

			@Override
			public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
				log.warn("Partitions assigned: " + partitions);
			}
		});

		return factory;
	}
}
