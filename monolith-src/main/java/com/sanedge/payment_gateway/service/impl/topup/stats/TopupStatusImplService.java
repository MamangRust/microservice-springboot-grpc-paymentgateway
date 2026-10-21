package com.sanedge.payment_gateway.service.impl.topup.stats;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.topup.stats.MonthTopupStatus;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusSuccess;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusSuccess;
import com.sanedge.payment_gateway.repository.topup.stats.TopupStatusRepository;
import com.sanedge.payment_gateway.service.topup.stats.status.TopupStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupStatusImplService implements TopupStatusService {
    private final TopupStatusRepository topupStatusRepository;

    @Override
    public ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatus req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();
        log.info("📊 Fetching monthly SUCCESS topup status for year={} month={}", year, month);

        validateYearMonth(year, month);

        LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                nextMonth.getMonthValue());

        try {
            List<TopupMonthStatusSuccess> data = topupStatusRepository.findMonthTopupStatusSuccess(
                    year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

            List<TopupResponseMonthStatusSuccess> response = data.stream()
                    .map(TopupResponseMonthStatusSuccess::from)
                    .toList();

            log.info("✅ Retrieved {} records for SUCCESS status month={} year={}", response.size(), month, year);

            return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly SUCCESS topup status")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly SUCCESS topup status month={} year={}", month, year, e);
            return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch monthly SUCCESS topup status")
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year) {
        log.info("📊 Fetching yearly SUCCESS topup status for year={}", year);
        validateYear(year);

        try {
            List<TopupYearStatusSuccess> data = topupStatusRepository.findYearlyTopupStatusSuccess(year);
            List<TopupResponseYearStatusSuccess> response = data.stream()
                    .map(TopupResponseYearStatusSuccess::from)
                    .toList();

            log.info("✅ Retrieved {} records for yearly SUCCESS status year={}", response.size(), year);

            return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched yearly SUCCESS topup status")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly SUCCESS topup status year={}", year, e);
            return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch yearly SUCCESS topup status")
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatus req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();
        log.info("📊 Fetching monthly FAILED topup status for year={} month={}", year, month);

        validateYearMonth(year, month);

        LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                nextMonth.getMonthValue());

        try {
            List<TopupMonthStatusFailed> data = topupStatusRepository.findMonthTopupStatusFailed(
                    year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

            List<TopupResponseMonthStatusFailed> response = data.stream()
                    .map(TopupResponseMonthStatusFailed::from)
                    .toList();

            log.info("✅ Retrieved {} records for FAILED status month={} year={}", response.size(), month, year);

            return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly FAILED topup status")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly FAILED topup status month={} year={}", month, year, e);
            return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch monthly FAILED topup status")
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(Long year) {
        log.info("📊 Fetching yearly FAILED topup status for year={}", year);
        validateYear(year);

        try {
            List<TopupYearStatusFailed> data = topupStatusRepository.findYearlyTopupStatusFailed(year);
            List<TopupResponseYearStatusFailed> response = data.stream()
                    .map(TopupResponseYearStatusFailed::from)
                    .toList();

            log.info("✅ Retrieved {} records for yearly FAILED status year={}", response.size(), year);

            return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched yearly FAILED topup status")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly FAILED topup status year={}", year, e);
            return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch yearly FAILED topup status")
                    .build();
        }
    }

    private void validateYearMonth(Long year, Integer month) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
        if (month == null || month < 1 || month > 12) {
            log.error("❌ Invalid month: {}", month);
            throw new IllegalArgumentException("Invalid month");
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
