package com.sanedge.payment_gateway.models.withdraw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawYearStatusSuccess {
    private String reportYear;
    private Long totalSuccess;
    private Long totalAmount;
}
