package com.productname.kafkaproducer.repository;

import com.productname.kafkaproducer.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
