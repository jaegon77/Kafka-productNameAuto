package com.productname.kafkaconsumer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.productname.kafkaconsumer.entity.TgtPdNmAuto;

public interface TgtPdNmAutoRepository extends JpaRepository<TgtPdNmAuto, Long> {
	Optional<TgtPdNmAuto> findByPdNoAndAttrBaseIdAndAttrValId(Long pdNo, Long attrBaseId, Long attrValId);
}
