package com.staffSync.application.product.dto;

import java.time.LocalDateTime;

import com.staffSync.domain.product.Product;
import com.staffSync.domain.product.StockStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // default 생성자
@AllArgsConstructor // 필드의 모든 파라미터 값 받는 생성자
public class ProductDto {

    private Long id;
    private String name;
    private String category;
    private Integer quantity;
    private Integer minStockLevel;
    private Double price;
    private StockStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getMinStockLevel(),
                product.getPrice(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

}
