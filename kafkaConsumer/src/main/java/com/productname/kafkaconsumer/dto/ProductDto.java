package com.productname.kafkaconsumer.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductDto {
	private Long id;
	private String name;
	private int price;
	private Category category;

	@Getter
	@Setter
	public static class Category {
		private Long id;
		private String name;
	}
}
