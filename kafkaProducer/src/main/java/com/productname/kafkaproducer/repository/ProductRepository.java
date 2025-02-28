package com.productname.kafkaproducer.repository;

import com.productname.kafkaproducer.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
