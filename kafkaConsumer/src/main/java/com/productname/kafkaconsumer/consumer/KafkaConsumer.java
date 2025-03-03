package com.productname.kafkaconsumer.consumer;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.productname.kafkaconsumer.dto.ProductDto;
import com.productname.kafkaconsumer.service.ProductNameAutoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

	private final ProductNameAutoService productNameAutoService;

	private final static String GROUP_ID = "product.name.auto.v1";
	private final static String PRODUCT_INSERT_TOPIC_NAME = "pd.nm.insert.v1";
	private final static String PRODUCT_NAME_UPDATE_TOPIC_NAME = "pd.nm.update.v1";
	private final static String GROUP_ID_DLT = "product.name.auto.v1.dlt";
	private final static String PRODUCT_INSERT_TOPIC_NAME_DLT = "pd.nm.insert.v1.dlt";
	private final static String PRODUCT_NAME_UPDATE_TOPIC_NAME_DLT = "pd.nm.update.v1.dlt";

	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@RetryableTopic(
			attempts = "3",
			backoff = @Backoff(delay = 5000, multiplier = 2.0),
			dltStrategy = DltStrategy.FAIL_ON_ERROR,
			dltTopicSuffix = ".dlt",
			retryTopicSuffix = ".retry"
	)
	@KafkaListener(topics = {PRODUCT_INSERT_TOPIC_NAME, PRODUCT_NAME_UPDATE_TOPIC_NAME},
					groupId = GROUP_ID,
					containerFactory = "kafkaListenerContainerFactory"
	)
	public void productNameRecordListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
		try {
			String jsonMessage = record.value();
			ProductDto productDto = objectMapper.readValue(jsonMessage, ProductDto.class);
			log.info(productDto.toString());
			productNameAutoService.process(productDto);

			ack.acknowledge();
		} catch (Exception e) {
			log.error("productNameRecordListener poll error", e);
		}
	}

	@KafkaListener(
			topics = {PRODUCT_INSERT_TOPIC_NAME_DLT, PRODUCT_NAME_UPDATE_TOPIC_NAME_DLT},
			groupId = GROUP_ID_DLT
	)
	public void productNameDltMessages(
		ConsumerRecord<String, String> record,
		@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
		@Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
		@Header(KafkaHeaders.OFFSET) Long offset,
		@Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage
	) {
		log.info("productNameDltMessages key: {}, value: {}, topic: {}, partition: {}, offset: {},"
				+ "errorMessage: {}", record.key(), record.value(), topic,
				 partitionId, offset, errorMessage);
		try {
			String jsonMessage = record.value();
			ProductDto productDto = objectMapper.readValue(jsonMessage, ProductDto.class);
			log.info(productDto.toString());
			productNameAutoService.dltProcess(productDto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
