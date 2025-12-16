package com.staffSync.product.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staffSync.product.application.ProductService;
import com.staffSync.product.application.dto.CreateProductRequest;
import com.staffSync.product.application.dto.DashboardDto;
import com.staffSync.product.application.dto.ProductDto;
import com.staffSync.product.application.dto.UpdateProductRequest;
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

    // ID로 상품 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
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

    // 상품 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Long id,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // 재고 증가
    /**
     * @Param id : 상품 ID
     * @param amount : 증가시킬 재고 수량
     * @return : 업데이트된 상품 정보
     */
    @PatchMapping("/{id}/stock/increase")
    public ResponseEntity<ProductDto> increaseStock(
            @PathVariable("id") Long id,
            @RequestParam Integer amount) {
        UpdateStockRequest request = new UpdateStockRequest(amount);
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    // 재고 감소
    /**
     * @param id     : 상품 ID
     * @param amount : 감소시킬 재고 수량
     * @return : 업데이트된 상품 정보
     */
    @PatchMapping("/{id}/stock/decrease")
    public ResponseEntity<ProductDto> decreaseStock(
            @PathVariable("id") Long id,
            @RequestParam Integer amount) {
        UpdateStockRequest request = new UpdateStockRequest(-amount);
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    // 재고 업데이트
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(
            @PathVariable("id") Long id,
            @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 카테고리별 상품 조회
     * 
     * @param category 카테고리명
     * @return 해당 카테고리의 상품 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    /**
     * 상품명으로 검색
     * 
     * @param keyword 키워드 검색
     * @return 검색된 상품 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
