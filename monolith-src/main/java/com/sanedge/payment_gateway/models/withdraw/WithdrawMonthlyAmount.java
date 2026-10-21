package com.sanedge.payment_gateway.models.withdraw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawMonthlyAmount {
    private String monthName;
    private Long totalAmount;
}