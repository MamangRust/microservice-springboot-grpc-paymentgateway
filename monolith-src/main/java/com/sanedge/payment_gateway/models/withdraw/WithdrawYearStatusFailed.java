package com.sanedge.payment_gateway.models.withdraw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawYearStatusFailed {
    private String reportYear;
    private Long totalFailed;
    private Long totalAmount;
}
