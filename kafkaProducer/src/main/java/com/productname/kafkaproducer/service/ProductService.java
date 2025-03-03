package com.productname.kafkaproducer.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.productname.kafkaproducer.dto.ProductRequest;
import com.productname.kafkaproducer.entity.Category;
import com.productname.kafkaproducer.entity.Product;
import com.productname.kafkaproducer.repository.CategoryRepository;
import com.productname.kafkaproducer.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final static String PRODUCT_INSERT_TOPIC_NAME = "pd.nm.insert.v1";
    private final static String PRODUCT_NAME_UPDATE_TOPIC_NAME = "pd.nm.update.v1";

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public Map<String, Object> handleProductOperation(ProductRequest request) {
        switch (request.getOperation()) {
            case ADD:
                return addProduct(request);
            case UPDATE:
                return updateProduct(request);
            default:
                throw new RuntimeException("Invalid product operation type.");
        }
    }

    private Map<String, Object> addProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found."));

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        sendToKafka(productRepository.save(product), PRODUCT_INSERT_TOPIC_NAME);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Product added successfully");

        return result;
    }

    private Map<String, Object> updateProduct(ProductRequest request) {
        Product product = productRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Product not found."));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found."));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        sendToKafka(productRepository.save(product), PRODUCT_NAME_UPDATE_TOPIC_NAME);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Product updated successfully.");
        return result;
    }

    private void sendToKafka(Product product, String topicName) {
        try {
            String jsonInString = objectMapper.writeValueAsString(product);
            kafkaTemplate.send(topicName, jsonInString);
            log.info("success sendToKafka");
        } catch (Exception e) {
            log.error("Product Name Producer sendToKafka error", e);
        }
    }
}
