package com.sanedge.payment_gateway.service.impl.transaction.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthMethod;
import com.sanedge.payment_gateway.models.transaction.TransactionYearMethod;
import com.sanedge.payment_gateway.repository.transaction.stats.TransactionMethodRepository;
import com.sanedge.payment_gateway.service.transaction.stats.method.TransactionMethodService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionMethodImplService implements TransactionMethodService {
    private final TransactionMethodRepository transactionMethodRepository;

    @Override
    public ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(Long year) {
        log.info("📊 Fetching monthly transaction methods for year={}", year);

        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year provided: {}", year);
            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Invalid year provided")
                    .build();
        }

        try {
            List<TransactionMonthMethod> methods = transactionMethodRepository.findMonthlyPaymentMethods(year);

            List<TransactionMonthMethodResponse> response = methods.stream()
                    .map(TransactionMonthMethodResponse::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction method records for year={}", response.size(), year);

            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("success")
                    .message("Successfully fetched monthly transaction methods for year=" + year)
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly transaction methods for year={}", year, e);
            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transaction methods for year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(Long year) {
        log.info("📊 Fetching yearly transaction methods until year={}", year);

        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year provided: {}", year);
            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionYearMethod> methods = transactionMethodRepository.findYearlyPaymentMethods(year);

            List<TransactionYearMethodResponse> response = methods.stream()
                    .map(TransactionYearMethodResponse::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction method records until year={}", response.size(), year);

            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("success")
                    .message("Successfully fetched yearly transaction methods until year=" + year)
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly transaction methods until year={}", year, e);
            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction methods until year=" + year)
                    .data(Collections.emptyList())
                    .build();
        }
    }
}
