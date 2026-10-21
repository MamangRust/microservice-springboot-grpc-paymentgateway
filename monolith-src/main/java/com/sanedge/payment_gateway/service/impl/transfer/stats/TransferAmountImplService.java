package com.sanedge.payment_gateway.service.impl.transfer.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferYearAmountResponse;
import com.sanedge.payment_gateway.models.transfer.TransferMonthAmount;
import com.sanedge.payment_gateway.models.transfer.TransferYearAmount;
import com.sanedge.payment_gateway.repository.transfer.stats.TransferAmountRepository;
import com.sanedge.payment_gateway.service.transfer.stats.amount.TransferAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferAmountImplService implements TransferAmountService {
    private final TransferAmountRepository transferAmountRepository;

    @Override
    public ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmounts(Long year) {
        log.info("📊 Fetching monthly transfer amounts for year={}", year);

        validateYear(year);

        try {
            List<TransferMonthAmount> amounts = transferAmountRepository.findMonthlyTransferAmounts(year);

            log.info("✅ Retrieved {} monthly transfer amount records for year={}", amounts.size(), year);

            List<TransferMonthAmountResponse> response = amounts.stream()
                    .map(TransferMonthAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                    .status("success")
                    .message("Monthly transfer amounts retrieved successfully!")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly transfer amounts for year={}", year);

            return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve monthly transfer amounts")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransferYearAmountResponse>> findYearlyAmounts(Long year) {
        log.info("📊 Fetching yearly transfer amounts until year={}", year);

        validateYear(year);

        try {
            List<TransferYearAmount> amounts = transferAmountRepository.findYearlyTransferAmounts(year);

            log.info("✅ Retrieved {} yearly transfer amount records until year={}", amounts.size(), year);

            List<TransferYearAmountResponse> response = amounts.stream()
                    .map(TransferYearAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TransferYearAmountResponse>>builder()
                    .status("success")
                    .message("Yearly transfer amounts retrieved successfully!")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly transfer amounts until year={}", year);

            return ApiResponse.<List<TransferYearAmountResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve yearly transfer amounts")
                    .data(null)
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