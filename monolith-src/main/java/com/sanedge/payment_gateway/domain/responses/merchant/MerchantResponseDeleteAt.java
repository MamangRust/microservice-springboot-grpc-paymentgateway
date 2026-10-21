package com.sanedge.payment_gateway.domain.responses.merchant;

import com.sanedge.payment_gateway.models.merchants.Merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseDeleteAt {
    private Long id;
    private String name;
    private Long userId;
    private String apiKey;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static MerchantResponseDeleteAt from(Merchant merchant) {
        return MerchantResponseDeleteAt.builder()
                .id(merchant.getMerchantId())
                .name(merchant.getName())
                .userId(merchant.getUserId())
                .apiKey(merchant.getApiKey())
                .status(merchant.getStatus() != null ? merchant.getStatus().name() : null)
                .createdAt(merchant.getCreatedAt() != null ? merchant.getCreatedAt().toString() : null)
                .updatedAt(merchant.getUpdatedAt() != null ? merchant.getUpdatedAt().toString() : null)
                .deletedAt(merchant.getDeletedAt() != null ? merchant.getDeletedAt().toString() : null)
                .build();
    }
}