package com.staffSync.application.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {
    private String name;
    private String category;
    private Integer quantity;
    private Integer minStockLevel;
    private Double price;
}
