package com.sanedge.payment_gateway.models.topup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopupYearMethod {
    private String reportYear;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;
}
