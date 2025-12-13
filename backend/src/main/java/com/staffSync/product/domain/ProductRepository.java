package com.staffSync.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * @findBy SELECT 쿼리 생성
     * @Name Product의 name 필드
     * @Containing LIKE'%keyword%' (포함검색)
     * @IgnoreCase 대소문자 구분 없음
     */

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

    // 상품명 검색 (대소문자 구분 없음)
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
