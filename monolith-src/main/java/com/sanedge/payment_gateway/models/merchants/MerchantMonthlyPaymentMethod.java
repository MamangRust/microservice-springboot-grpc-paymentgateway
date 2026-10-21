package com.sanedge.payment_gateway.models.merchants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantMonthlyPaymentMethod {
    private String monthName;
    private String paymentMethod;
    private Long totalAmount;
}
