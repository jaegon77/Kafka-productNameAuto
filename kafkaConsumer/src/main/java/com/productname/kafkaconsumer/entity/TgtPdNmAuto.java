package com.productname.kafkaconsumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "TGT_PD_NM_AUTO", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"pdNo", "attrBaseId", "attrValId"})
})
@Entity
public class TgtPdNmAuto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@Column(nullable = false)
	private Long pdNo;

	@Column(nullable = false)
	private Long attrBaseId;

	@Column(nullable = false)
	private String attrBaseNm;

	@Column(nullable = false)
	private Long attrValId;

	@Column(nullable = false)
	private String attrValNm;
}
