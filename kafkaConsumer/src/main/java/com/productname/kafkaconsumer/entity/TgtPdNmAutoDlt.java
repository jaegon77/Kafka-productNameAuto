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
@Table(name = "TGT_PD_NM_AUTO_DLT")
@Entity
public class TgtPdNmAutoDlt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long pdNo;

	@Column(nullable = false)
	private String pdNm;

	@Column(nullable = false)
	private Long categoryId;
}
