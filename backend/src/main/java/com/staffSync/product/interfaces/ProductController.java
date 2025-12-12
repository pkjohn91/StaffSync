package com.staffSync.product.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staffSync.product.application.ProductService;
import com.staffSync.product.application.dto.CreateProductRequest;
import com.staffSync.product.application.dto.DashboardDto;
import com.staffSync.product.application.dto.ProductDto;
import com.staffSync.product.application.dto.UpdateStockRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    // 대시보드 데이터 조회
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(productService.getDashboard());
    }

    // 전체 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 재고 부족 상품 조회
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    // 상품 등록
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // 재고 업데이트
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
