package com.staffSync.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.staffSync.product.domain.Product;
import com.staffSync.product.domain.StockStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리별 조회
    List<Product> findByCategory(String category);

    // 재고 상태별 조회
    List<Product> findByStatus(StockStatus status);

    // 재고 부족 상품 조회
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel")
    List<Product> findLowStockProducts();

    // 품절 상품 개수
    long countByStatus(StockStatus status);

    // 카테고리별 재고 현황
    @Query("SELECT p.category, SUM(p.quantity) FROM Product p GROUP BY p.category")
    List<Object[]> getStockByCategory();
}
