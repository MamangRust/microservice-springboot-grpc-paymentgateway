package com.sanedge.payment_gateway.models.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardMonthBalance {
    private String monthName;
    private Long totalBalance;
}
