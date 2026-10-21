package com.sanedge.payment_gateway.models.topup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopupYearStatusSuccess {
    private String reportYear;
    private Long totalSuccess;
    private Long totalAmount;
}
