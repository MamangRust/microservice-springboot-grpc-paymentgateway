package com.sanedge.payment_gateway.service.impl.withdraw.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthlyAmount;
import com.sanedge.payment_gateway.models.withdraw.WithdrawYearlyAmount;
import com.sanedge.payment_gateway.repository.withdraw.stats.WithdrawAmountRepository;
import com.sanedge.payment_gateway.service.withdraw.stats.amount.WithdrawAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawAmountImplService implements WithdrawAmountService {

    private final WithdrawAmountRepository withdrawAmountRepository;

    @Override
    public ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyWithdraws(Long year) {
        log.info("📊 Fetching monthly withdraw amounts for year={}", year);

        try {
            validateYear(year);

            List<WithdrawMonthlyAmount> amounts = withdrawAmountRepository.findMonthlyWithdraws(year);

            log.info("✅ Retrieved {} monthly withdraw amount records for year={}", amounts.size(), year);

            List<WithdrawMonthlyAmountResponse> response = amounts.stream()
                    .map(WithdrawMonthlyAmountResponse::from)
                    .toList();

            return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                    .status("success")
                    .message("Monthly withdraw amounts retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid input year={}", year);
            return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly withdraw amounts for year={}", year, e);
            return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve monthly withdraw amounts")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyWithdraws(Long year) {
        log.info("📊 Fetching yearly withdraw amounts until year={}", year);

        try {
            validateYear(year);

            List<WithdrawYearlyAmount> amounts = withdrawAmountRepository.findYearlyWithdraws(year);

            log.info("✅ Retrieved {} yearly withdraw amount records until year={}", amounts.size(), year);

            List<WithdrawYearlyAmountResponse> response = amounts.stream()
                    .map(WithdrawYearlyAmountResponse::from)
                    .toList();

            return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                    .status("success")
                    .message("Yearly withdraw amounts retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid input year={}", year);
            return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly withdraw amounts until year={}", year, e);
            return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Failed to retrieve yearly withdraw amounts")
                    .data(null)
                    .build();
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
