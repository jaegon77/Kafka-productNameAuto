package com.productname.kafkaconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

	private final static String PRODUCT_INSERT_TOPIC_NAME = "pd.insert.v1";
	private final static String PRODUCT_NAME_UPDATE_TOPIC_NAME = "pd.nm.update.v1";
	private final static String GROUP_ID = "product.name.auto.v1";

	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@KafkaListener(topics = {PRODUCT_INSERT_TOPIC_NAME, PRODUCT_NAME_UPDATE_TOPIC_NAME}, groupId = GROUP_ID)
	public void productNameRecordListener(String jsonMessage) {
		try {
			ProductDto productDto = objectMapper.readValue(jsonMessage, ProductDto.class);
			log.info(productDto.toString());
			productNameAutoService.process(productDto);
		} catch (Exception e) {
			log.error("productNameRecordListener poll error", e);
		}
	}
}
