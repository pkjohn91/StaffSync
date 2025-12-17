package com.staffSync.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staffSync.application.product.ProductService;
import com.staffSync.application.product.dto.CreateProductRequest;
import com.staffSync.application.product.dto.DashboardDto;
import com.staffSync.application.product.dto.ProductDto;
import com.staffSync.application.product.dto.UpdateStockRequest;
import com.staffSync.domain.product.Product;
import com.staffSync.domain.product.StockStatus;
import com.staffSync.domain.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        // 테스트용 상품 데이터 준비
        testProduct1 = new Product("노트북", "전자제품", 50, 10, 1000000.0);
        testProduct2 = new Product("마우스", "전자제품", 5, 10, 20000.0);
        testProduct3 = new Product("키보드", "전자제품", 0, 5, 50000.0);
    }

    @Nested
    @DisplayName("getDashboard 메서드는")
    class Describe_getDashboard {

        @Test
        @DisplayName("대시보드 통계 데이터를 정확하게 반환한다")
        void it_returns_dashboard_statistics() {
            // given
            List<Product> products = Arrays.asList(testProduct1, testProduct2, testProduct3);
            given(productRepository.findAll()).willReturn(products);
            given(productRepository.countByStatus(StockStatus.IN_STOCK)).willReturn(1L);
            given(productRepository.countByStatus(StockStatus.LOW_STOCK)).willReturn(1L);
            given(productRepository.countByStatus(StockStatus.OUT_OF_STOCK)).willReturn(1L);

            // when
            DashboardDto dashboard = productService.getDashboard();

            // then
            assertThat(dashboard).isNotNull();
            assertThat(dashboard.getTotalProducts()).isEqualTo(3);
            assertThat(dashboard.getInStockCount()).isEqualTo(1);
            assertThat(dashboard.getLowStockCount()).isEqualTo(1);
            assertThat(dashboard.getOutOfStockCount()).isEqualTo(1);

            // 총 재고 가치 검증: (50 * 1000000) + (5 * 20000) + (0 * 50000) = 50,100,000
            assertThat(dashboard.getTotalInventoryValue()).isEqualTo(50100000.0);

            // 카테고리별 재고 검증
            assertThat(dashboard.getStockByCategory()).containsEntry("전자제품", 55L);

            verify(productRepository).findAll();
            verify(productRepository).countByStatus(StockStatus.IN_STOCK);
            verify(productRepository).countByStatus(StockStatus.LOW_STOCK);
            verify(productRepository).countByStatus(StockStatus.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("상품이 없을 때 빈 대시보드 데이터를 반환한다")
        void it_returns_empty_dashboard_when_no_products() {
            // given
            given(productRepository.findAll()).willReturn(Arrays.asList());
            given(productRepository.countByStatus(any())).willReturn(0L);

            // when
            DashboardDto dashboard = productService.getDashboard();

            // then
            assertThat(dashboard.getTotalProducts()).isEqualTo(0);
            assertThat(dashboard.getInStockCount()).isEqualTo(0);
            assertThat(dashboard.getTotalInventoryValue()).isEqualTo(0.0);
            assertThat(dashboard.getStockByCategory()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllProducts 메서드는")
    class Describe_getAllProducts {

        @Test
        @DisplayName("모든 상품 목록을 DTO로 변환하여 반환한다")
        void it_returns_all_products_as_dto() {
            // given
            List<Product> products = Arrays.asList(testProduct1, testProduct2);
            given(productRepository.findAll()).willReturn(products);

            // when
            List<ProductDto> result = productService.getAllProducts();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("노트북");
            assertThat(result.get(1).getName()).isEqualTo("마우스");
            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("상품이 없을 때 빈 리스트를 반환한다")
        void it_returns_empty_list_when_no_products() {
            // given
            given(productRepository.findAll()).willReturn(Arrays.asList());

            // when
            List<ProductDto> result = productService.getAllProducts();

            // then
            assertThat(result).isEmpty();
            verify(productRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getLowStockProducts 메서드는")
    class Describe_getLowStockProducts {

        @Test
        @DisplayName("재고 부족 상품 목록을 반환한다")
        void it_returns_low_stock_products() {
            // given
            List<Product> lowStockProducts = Arrays.asList(testProduct2, testProduct3);
            given(productRepository.findLowStockProducts()).willReturn(lowStockProducts);

            // when
            List<ProductDto> result = productService.getLowStockProducts();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("마우스");
            assertThat(result.get(1).getName()).isEqualTo("키보드");
            verify(productRepository).findLowStockProducts();
        }

        @Test
        @DisplayName("재고 부족 상품이 없을 때 빈 리스트를 반환한다")
        void it_returns_empty_list_when_no_low_stock_products() {
            // given
            given(productRepository.findLowStockProducts()).willReturn(Arrays.asList());

            // when
            List<ProductDto> result = productService.getLowStockProducts();

            // then
            assertThat(result).isEmpty();
            verify(productRepository).findLowStockProducts();
        }
    }

    @Nested
    @DisplayName("createProduct 메서드는")
    class Describe_createProduct {

        @Test
        @DisplayName("새로운 상품을 생성하고 DTO로 반환한다")
        void it_creates_and_returns_product() {
            // given
            CreateProductRequest request = new CreateProductRequest();
            request.setName("모니터");
            request.setCategory("전자제품");
            request.setQuantity(30);
            request.setMinStockLevel(10);
            request.setPrice(300000.0);

            Product savedProduct = new Product("모니터", "전자제품", 30, 10, 300000.0);
            given(productRepository.save(any(Product.class))).willReturn(savedProduct);

            // when
            ProductDto result = productService.createProduct(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("모니터");
            assertThat(result.getCategory()).isEqualTo("전자제품");
            assertThat(result.getQuantity()).isEqualTo(30);
            assertThat(result.getPrice()).isEqualTo(300000.0);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("유효하지 않은 데이터로 상품 생성 시 예외가 발생한다")
        void it_throws_exception_when_invalid_data() {
            // given
            CreateProductRequest request = new CreateProductRequest();
            request.setName("잘못된 상품");
            request.setCategory("전자제품");
            request.setQuantity(-10); // 음수 수량
            request.setMinStockLevel(10);
            request.setPrice(100000.0);

            // when & then
            assertThatThrownBy(() -> productService.createProduct(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 수량을 0 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("updateStock 메서드는")
    class Describe_updateStock {

        @Test
        @DisplayName("재고를 추가할 수 있다")
        void it_can_add_stock() {
            // given
            Long productId = 1L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setAmount(20);

            Product product = new Product("노트북", "전자제품", 10, 5, 1000000.0);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductDto result = productService.updateStock(productId, request);

            // then
            assertThat(result.getQuantity()).isEqualTo(30); // 10 + 20
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("재고를 차감할 수 있다")
        void it_can_reduce_stock() {
            // given
            Long productId = 1L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setAmount(-5);

            Product product = new Product("노트북", "전자제품", 10, 5, 1000000.0);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductDto result = productService.updateStock(productId, request);

            // then
            assertThat(result.getQuantity()).isEqualTo(5); // 10 - 5
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("재고가 부족할 때 차감 시 예외가 발생한다")
        void it_throws_exception_when_insufficient_stock() {
            // given
            Long productId = 1L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setAmount(-50); // 현재 재고보다 많이 차감

            Product product = new Product("노트북", "전자제품", 10, 5, 1000000.0);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() -> productService.updateStock(productId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고가 부족합니다");
        }

        @Test
        @DisplayName("존재하지 않는 상품의 재고 업데이트 시 예외가 발생한다")
        void it_throws_exception_when_product_not_found() {
            // given
            Long productId = 999L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setAmount(10);

            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.updateStock(productId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("amount가 0일 때는 재고가 변경되지 않는다")
        void it_does_not_change_stock_when_amount_is_zero() {
            // given
            Long productId = 1L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setAmount(0);

            Product product = new Product("노트북", "전자제품", 10, 5, 1000000.0);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            ProductDto result = productService.updateStock(productId, request);

            // then
            assertThat(result.getQuantity()).isEqualTo(10); // 변경 없음
        }
    }

    @Nested
    @DisplayName("deleteProduct 메서드는")
    class Describe_deleteProduct {

        @Test
        @DisplayName("존재하는 상품을 삭제할 수 있다")
        void it_deletes_existing_product() {
            // given
            Long productId = 1L;
            given(productRepository.existsById(productId)).willReturn(true);
            willDoNothing().given(productRepository).deleteById(productId);

            // when
            productService.deleteProduct(productId);

            // then
            verify(productRepository).existsById(productId);
            verify(productRepository).deleteById(productId);
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생한다")
        void it_throws_exception_when_product_not_found() {
            // given
            Long productId = 999L;
            given(productRepository.existsById(productId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다");

            verify(productRepository).existsById(productId);
            verify(productRepository, never()).deleteById(any());
        }
    }
}
