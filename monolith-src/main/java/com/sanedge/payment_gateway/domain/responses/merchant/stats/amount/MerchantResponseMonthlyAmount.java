package com.sanedge.payment_gateway.domain.responses.merchant.stats.amount;

import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseMonthlyAmount {
    private String monthName;
    private Long totalAmount;

    public static MerchantResponseMonthlyAmount from(MerchantMonthlyAmount model) {
        return MerchantResponseMonthlyAmount.builder()
                .monthName(model.getMonthName())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}