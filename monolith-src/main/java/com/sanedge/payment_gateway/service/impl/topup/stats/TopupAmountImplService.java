package com.sanedge.payment_gateway.service.impl.topup.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.payment_gateway.models.topup.TopupMonthAmount;
import com.sanedge.payment_gateway.models.topup.TopupYearAmount;
import com.sanedge.payment_gateway.repository.topup.stats.TopupAmountRepository;
import com.sanedge.payment_gateway.service.topup.stats.amount.TopupAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupAmountImplService implements TopupAmountService {
    private final TopupAmountRepository topupAmountRepository;

    @Override
    public ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(Long year) {
        log.info("📊 Fetching monthly topup amounts for year={}", year);

        validateYear(year);

        try {
            List<TopupMonthAmount> amounts = topupAmountRepository.findMonthlyTopupAmounts(year);

            log.info("✅ Retrieved {} monthly topup amount records for year={}", amounts.size(), year);

            List<TopupMonthAmountResponse> response = amounts.stream()
                    .map(TopupMonthAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly topup amounts for year=" + year)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly topup amounts for year={}", year, e);
            return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly topup amounts for year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(Long year) {
        log.info("📊 Fetching yearly topup amounts until year={}", year);

        validateYear(year);

        try {
            List<TopupYearAmount> amounts = topupAmountRepository.findYearlyTopupAmounts(year);

            log.info("✅ Retrieved {} yearly topup amount records until year={}", amounts.size(), year);

            List<TopupYearlyAmountResponse> response = amounts.stream()
                    .map(TopupYearlyAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched yearly topup amounts until year=" + year)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly topup amounts until year={}", year, e);
            return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly topup amounts until year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
