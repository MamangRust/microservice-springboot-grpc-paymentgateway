package com.sanedge.payment_gateway.domain.responses.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMeta {
    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public static PaginationMeta fromSpringPage(Page<?> pageData) {
        return new PaginationMeta(
                pageData.getNumber() + 1,
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages());
    }
}