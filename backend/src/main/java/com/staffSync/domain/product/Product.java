package com.staffSync.domain.product;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Integer quantity; // 현재 재고 수량

    @Column(nullable = false)
    private Integer minStockLevel; // 최소 재고 수량

    @Column(nullable = false)
    private Double price;

    @Column
    @Enumerated(EnumType.STRING)
    private StockStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 생성자
    public Product(String name, String category, Integer quantity, Integer minStockLevel, Double price) {

        validateQuantity(quantity);
        validateMinStockLevel(minStockLevel);
        validatePrice(price);

        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.price = price;
        this.status = calculateStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    // 재고 추가
    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("재고 추가 수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
        this.status = calculateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    // 재고 차감
    public void reducetStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("재고 차감 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= amount;
        this.status = calculateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    // 재고 상태 계산
    private StockStatus calculateStatus() {
        if (this.quantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (this.quantity <= this.minStockLevel) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    // 상품 정보 업데이트
    public void updateDetails(String name, String category, Double price, Integer minStockLevel) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (category != null && !category.isBlank()) {
            this.category = category;
        }
        if (price != null) {
            validatePrice(price);
            this.price = price;
        }
        if (minStockLevel != null) {
            validateMinStockLevel(minStockLevel);
            this.minStockLevel = minStockLevel;
        }
        this.status = calculateStatus(); // 최소 재고 수준 변경 시 상태가 바뀔 수 있음
        this.updatedAt = LocalDateTime.now();
    }

    // 검증(Validation) 메서드
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("재고 수량을 0 이상이어야 합니다.");
        }
    }

    private void validateMinStockLevel(Integer minStockLevel) {
        if (minStockLevel == null || minStockLevel < 0) {
            throw new IllegalArgumentException("최소 재고 수량을 0 이상이어야 합니다.");
        }
    }

    private void validatePrice(Double price) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
    }
}
