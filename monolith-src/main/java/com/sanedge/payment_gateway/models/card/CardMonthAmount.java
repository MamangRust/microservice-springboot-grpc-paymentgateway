package com.sanedge.payment_gateway.models.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardMonthAmount {
    private String monthName;
    private Long totalAmount;
}
