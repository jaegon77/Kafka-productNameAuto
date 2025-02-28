package com.productname.kafkaproducer.controller;

import com.productname.kafkaproducer.dto.ProductRequest;
import com.productname.kafkaproducer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductNameAutoController {

    private final ProductService productService;

    @PostMapping("/product")
    public Object handleProductOperation(@RequestBody ProductRequest request) {
        return productService.handleProductOperation(request);
    }

}
