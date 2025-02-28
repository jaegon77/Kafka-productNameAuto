package com.productname.kafkaconsumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "ATTR_VAL")
@Entity
public class AttrVal {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private Long categoryId;

	@Column(nullable = false)
	private Long attrBaseId;

	@Column(nullable = false)
	private String attrBaseNm;

	@Column(nullable = false)
	private Long attrValId;

	@Column(nullable = false)
	private String attrValNm;

	@Column(nullable = false)
	private String pdUnitNm;
}
