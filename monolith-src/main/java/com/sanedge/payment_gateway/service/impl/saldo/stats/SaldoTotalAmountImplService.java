package com.sanedge.payment_gateway.service.impl.saldo.stats;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.saldo.MonthTotalSaldoBalance;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;
import com.sanedge.payment_gateway.models.saldo.SaldoMonthTotalBalance;
import com.sanedge.payment_gateway.models.saldo.SaldoYearTotalBalance;
import com.sanedge.payment_gateway.repository.saldo.stats.SaldoTotalAmountRepository;
import com.sanedge.payment_gateway.service.saldo.stats.SaldoTotalAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaldoTotalAmountImplService implements SaldoTotalAmountService {
    private final SaldoTotalAmountRepository saldoTotalAmountRepository;

    @Override
    public ApiResponse<List<SaldoMonthTotalBalanceResponse>> findMonthTotalBalance(MonthTotalSaldoBalance req) {
        try {
            log.info("📊 Fetching monthly total saldo balance for year: {}, month: {}", req.getYear(), req.getMonth());

            validateYearMonth(req.getYear(), req.getMonth());

            LocalDate currentMonth = LocalDate.of(req.getYear().intValue(), req.getMonth(), 1);
            LocalDate nextMonth = currentMonth.plusMonths(1);

            log.debug("🗓️ Querying data for: {}/{} and {}/{}",
                    req.getYear(), req.getMonth(), (long) nextMonth.getYear(), nextMonth.getMonthValue());

            List<SaldoMonthTotalBalance> monthTotals = saldoTotalAmountRepository.findMonthlyTotalSaldoBalance(
                    req.getYear(), req.getMonth(),
                    (long) nextMonth.getYear(), nextMonth.getMonthValue());

            log.info("✅ Found {} monthly total balance records", monthTotals.size());

            List<SaldoMonthTotalBalanceResponse> response = monthTotals.stream()
                    .map(SaldoMonthTotalBalanceResponse::from)
                    .toList();

            return ApiResponse.<List<SaldoMonthTotalBalanceResponse>>builder()
                    .status("success")
                    .message("Get monthly total balance success")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly total saldo balance for year={}, month={}", req.getYear(),
                    req.getMonth(), e);
            return ApiResponse.<List<SaldoMonthTotalBalanceResponse>>builder()
                    .status("error")
                    .message("Failed to get monthly total balance")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<SaldoYearTotalBalanceResponse>> findYearTotalBalance(Long year) {
        try {
            log.info("📊 Fetching yearly total saldo balance for year: {}", year);

            validateYear(year);

            List<SaldoYearTotalBalance> yearTotals = saldoTotalAmountRepository.findYearlyTotalSaldoBalance(year);

            log.info("✅ Found {} yearly total balance records for year {}", yearTotals.size(), year);

            List<SaldoYearTotalBalanceResponse> response = yearTotals.stream()
                    .map(SaldoYearTotalBalanceResponse::from)
                    .toList();

            return ApiResponse.<List<SaldoYearTotalBalanceResponse>>builder()
                    .status("success")
                    .message("Get yearly total balance success")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly total saldo balance for year={}", year, e);
            return ApiResponse.<List<SaldoYearTotalBalanceResponse>>builder()
                    .status("error")
                    .message("Failed to get yearly total balance")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    private void validateYearMonth(Long year, Integer month) {
        if (year == null || month == null) {
            log.error("❌ Year or month is null | year={}, month={}", year, month);
            throw new IllegalArgumentException("Year and month cannot be null");
        }

        if (year < 1900 || year > 2100) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year: " + year);
        }

        if (month < 1 || month > 12) {
            log.error("❌ Invalid month: {}", month);
            throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}