package com.staffSync.product.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staffSync.product.application.dto.CreateProductRequest;
import com.staffSync.product.application.dto.DashboardDto;
import com.staffSync.product.application.dto.ProductDto;
import com.staffSync.product.application.dto.UpdateStockRequest;
import com.staffSync.product.domain.Product;
import com.staffSync.product.domain.StockStatus;
import com.staffSync.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // 대시보드 데이터 조회
    public DashboardDto getDashboard() {
        List<Product> allProducts = productRepository.findAll();

        long totalProducts = allProducts.size();
        long inStockCount = productRepository.countByStatus(StockStatus.IN_STOCK);
        long lowStockCount = productRepository.countByStatus(StockStatus.LOW_STOCK);
        long outOfStockCount = productRepository.countByStatus(StockStatus.OUT_OF_STOCK);

        // 총 재고 가치 계산
        double totalValue = allProducts.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();

        // 카테고리별 재고 현황
        Map<String, Long> stockByCategory = allProducts.stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.summingLong(Product::getQuantity)));

        return new DashboardDto(
                totalProducts,
                inStockCount,
                lowStockCount,
                outOfStockCount,
                totalValue,
                stockByCategory);
    }

    // 상품 전체 조회
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDto::from)
                .collect(Collectors.toList());
    }

    // 재고 부족 상품 조회
    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(ProductDto::from)
                .collect(Collectors.toList());
    }

    // 상품 등록
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getCategory(),
                request.getQuantity(),
                request.getMinStockLevel(),
                request.getPrice());

        Product savedProduct = productRepository.save(product);
        return ProductDto.from(savedProduct);
    }

    // 재고 업데이트 (추가 또는 차감)
    @Transactional
    public ProductDto updateStock(Long productId, UpdateStockRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (request.getAmount() > 0) {
            product.addStock(request.getAmount());
        } else if (request.getAmount() < 0) {
            product.reducetStock(Math.abs(request.getAmount()));
        }

        return ProductDto.from(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }
        productRepository.deleteById(productId);
    }
}
