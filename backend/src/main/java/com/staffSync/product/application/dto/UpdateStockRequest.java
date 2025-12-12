package com.staffSync.product.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStockRequest {
    private Integer amount; // 양수: 재고 추가, 음수: 재고 감소
}
