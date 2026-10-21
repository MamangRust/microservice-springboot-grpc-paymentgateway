package com.sanedge.payment_gateway.service.impl.saldo.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.payment_gateway.models.saldo.SaldoMonthBalance;
import com.sanedge.payment_gateway.models.saldo.SaldoYearBalance;
import com.sanedge.payment_gateway.repository.saldo.stats.SaldoBalanceRepository;
import com.sanedge.payment_gateway.service.saldo.stats.SaldoBalanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaldoBalanceImplService implements SaldoBalanceService {
    private final SaldoBalanceRepository saldoBalanceRepository;

    @Override
    public ApiResponse<List<SaldoMonthBalanceResponse>> getMonthBalance(Long year) {
        try {
            log.info("📊 Fetching monthly saldo balance for year: {}", year);

            validateYear(year);

            List<SaldoMonthBalance> monthBalances = saldoBalanceRepository.findMonthlySaldoBalances(year);

            log.info("✅ Found {} monthly saldo balance records for year {}", monthBalances.size(), year);

            List<SaldoMonthBalanceResponse> response = monthBalances.stream()
                    .map(SaldoMonthBalanceResponse::from)
                    .toList();

            return ApiResponse.<List<SaldoMonthBalanceResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly saldo balance")
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly saldo balance for year={}", year, e);
            return ApiResponse.<List<SaldoMonthBalanceResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly saldo balance")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<SaldoYearBalanceResponse>> getYearBalance(Long year) {
        try {
            log.info("📊 Fetching yearly saldo balance for year: {}", year);

            validateYear(year);

            List<SaldoYearBalance> yearBalances = saldoBalanceRepository.findYearlySaldoBalances(year);

            log.info("✅ Found {} yearly saldo balance records for year {}", yearBalances.size(), year);

            List<SaldoYearBalanceResponse> response = yearBalances.stream()
                    .map(SaldoYearBalanceResponse::from)
                    .toList();

            return ApiResponse.<List<SaldoYearBalanceResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched yearly saldo balance")
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly saldo balance for year={}", year, e);
            return ApiResponse.<List<SaldoYearBalanceResponse>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch yearly saldo balance")
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
