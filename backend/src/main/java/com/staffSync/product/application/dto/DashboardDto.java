package com.staffSync.product.application.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private long totalProducts; // 전체 상품 수
    private long inStockCount; // 재고 충분
    private long lowStockCount; // 재고 부족
    private long outOfStockCount; // 품절
    private Double totalInventoryValue; // 총 재고 가치
    private Map<String, Long> stockByCategory; // 카테고리별 재고 현황

    // 생성자
}
