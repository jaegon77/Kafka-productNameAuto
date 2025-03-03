package com.productname.kafkaconsumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productname.kafkaconsumer.entity.TgtPdNmAutoDlt;

public interface TgtPdNmAutoDltRepository extends JpaRepository<TgtPdNmAutoDlt, Long> {
}
