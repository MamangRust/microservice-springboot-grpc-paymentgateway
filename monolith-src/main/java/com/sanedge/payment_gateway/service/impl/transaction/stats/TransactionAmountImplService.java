package com.sanedge.payment_gateway.service.impl.transaction.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthAmount;
import com.sanedge.payment_gateway.models.transaction.TransactionYearAmount;
import com.sanedge.payment_gateway.repository.transaction.stats.TransactionAmountRepository;
import com.sanedge.payment_gateway.service.transaction.stats.amount.TransactionAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionAmountImplService implements TransactionAmountService {
    private final TransactionAmountRepository transactionAmountRepository;

    @Override
    public ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(Long year) {
        log.info("📊 Fetching monthly transaction amounts for year={}", year);

        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionMonthAmount> amounts = transactionAmountRepository.findMonthlyAmounts(year);

            List<TransactionMonthAmountResponse> response = amounts.stream()
                    .map(TransactionMonthAmountResponse::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction amount records for year={}", response.size(), year);

            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly transaction amounts for year=" + year)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly transaction amounts for year={}", year, e);
            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transaction amounts for year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(Long year) {
        log.info("📊 Fetching yearly transaction amounts until year={}", year);

        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionYearAmount> amounts = transactionAmountRepository.findYearlyAmounts(year);

            List<TransactionYearlyAmountResponse> response = amounts.stream()
                    .map(TransactionYearlyAmountResponse::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction amount records until year={}", response.size(), year);

            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("success")
                    .message("Successfully fetched yearly transaction amounts until year=" + year)
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly transaction amounts until year={}", year, e);
            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction amounts until year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }
}
