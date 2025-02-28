package com.productname.kafkaproducer.dto;

import com.productname.kafkaproducer.common.OperationType;
import lombok.Data;

@Data
public class ProductRequest {
    private Long id;
    private String name;
    private int price;
    private Long categoryId;
    private OperationType operation;
}
