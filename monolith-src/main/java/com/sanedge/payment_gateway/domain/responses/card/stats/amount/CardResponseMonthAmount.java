package com.sanedge.payment_gateway.domain.responses.card.stats.amount;

import com.sanedge.payment_gateway.models.card.CardMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseMonthAmount {
    private String monthName;
    private Long totalAmount;

    public static CardResponseMonthAmount from(CardMonthAmount model) {
        return CardResponseMonthAmount.builder()
                .monthName(model.getMonthName())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}