package com.staffSync.product.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.staffSync.product.domain.Product;
import com.staffSync.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

// 테스트 초기 데이터
@Component
@RequiredArgsConstructor
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // 이미 데이터가 있으면 스킵
        if (productRepository.count() > 0) {
            return;
        }

        // 샘플 데이터 추가
        productRepository.save(new Product("노트북", "전자제품", 50, 10, 1500000.0));
        productRepository.save(new Product("마우스", "전자제품", 5, 20, 30000.0));
        productRepository.save(new Product("키보드", "전자제품", 8, 15, 80000.0));
        productRepository.save(new Product("모니터", "전자제품", 0, 5, 300000.0));
        productRepository.save(new Product("의자", "가구", 25, 10, 200000.0));
        productRepository.save(new Product("책상", "가구", 15, 10, 350000.0));
        productRepository.save(new Product("노트", "문구", 100, 50, 3000.0));
        productRepository.save(new Product("펜", "문구", 200, 100, 1500.0));
    }
}
