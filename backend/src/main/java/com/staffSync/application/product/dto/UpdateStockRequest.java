package com.staffSync.application.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter // 재고 증감 테스트
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {
    private Integer amount; // 양수: 재고 추가, 음수: 재고 감소
}
