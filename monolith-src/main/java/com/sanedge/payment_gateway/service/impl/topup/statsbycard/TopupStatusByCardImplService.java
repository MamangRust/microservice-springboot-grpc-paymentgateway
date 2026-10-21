package com.sanedge.payment_gateway.service.impl.topup.statsbycard;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusSuccess;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusSuccess;
import com.sanedge.payment_gateway.repository.topup.statsbycard.TopupStatusByCardRepository;
import com.sanedge.payment_gateway.service.topup.stats.status.TopupStatusByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupStatusByCardImplService implements TopupStatusByCardService {
        private final TopupStatusByCardRepository topupStatusByCardRepository;

        @Override
        public ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(
                        MonthTopupStatusCardNumber req) {
                String cardNumber = req.getCardNumber();
                Long year = (long) req.getYear();
                int month = req.getMonth();
                log.info("📊 Fetching monthly SUCCESS topup status for card={} year={} month={}", cardNumber, year,
                                month);

                validateCard(cardNumber);
                validateYearMonth(year, month);

                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                try {
                        List<TopupMonthStatusSuccess> data = topupStatusByCardRepository
                                        .findMonthTopupStatusSuccessByCard(
                                                        cardNumber, year, month, (long) nextMonth.getYear(),
                                                        nextMonth.getMonthValue());
                        List<TopupResponseMonthStatusSuccess> response = data.stream()
                                        .map(TopupResponseMonthStatusSuccess::from)
                                        .toList();

                        log.info("✅ Retrieved {} SUCCESS records for card={} month={} year={}", response.size(),
                                        cardNumber, month,
                                        year);

                        return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly SUCCESS topup status for card="
                                                        + cardNumber)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly SUCCESS topup status for card={} month={} year={}",
                                        cardNumber, month,
                                        year, e);
                        return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly SUCCESS topup status for card=" + cardNumber)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(
                        YearTopupStatusCardNumber req) {
                String cardNumber = req.getCardNumber();
                Long year = (long) req.getYear();

                log.info("📊 Fetching yearly SUCCESS topup status for card={} year={}", cardNumber, year);

                validateCard(cardNumber);
                validateYear(year);

                try {
                        List<TopupYearStatusSuccess> data = topupStatusByCardRepository
                                        .findYearlyTopupStatusSuccessByCard(cardNumber, year);
                        List<TopupResponseYearStatusSuccess> response = data.stream()
                                        .map(TopupResponseYearStatusSuccess::from)
                                        .toList();

                        log.info("✅ Retrieved {} yearly SUCCESS records for card={} year={}", response.size(),
                                        cardNumber, year);

                        return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly SUCCESS topup status for card="
                                                        + cardNumber)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly SUCCESS topup status for card={} year={}", cardNumber,
                                        year, e);
                        return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly SUCCESS topup status for card=" + cardNumber)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatusCardNumber req) {
                String cardNumber = req.getCardNumber();
                Long year = (long) req.getYear();
                int month = req.getMonth();
                log.info("📊 Fetching monthly FAILED topup status for card={} year={} month={}", cardNumber, year,
                                month);

                validateCard(cardNumber);
                validateYearMonth(year, month);

                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                try {
                        List<TopupMonthStatusFailed> data = topupStatusByCardRepository
                                        .findMonthTopupStatusFailedByCard(
                                                        cardNumber, year, month, (long) nextMonth.getYear(),
                                                        nextMonth.getMonthValue());
                        List<TopupResponseMonthStatusFailed> response = data.stream()
                                        .map(TopupResponseMonthStatusFailed::from)
                                        .toList();

                        log.info("✅ Retrieved {} FAILED records for card={} month={} year={}", response.size(),
                                        cardNumber, month,
                                        year);

                        return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly FAILED topup status for card="
                                                        + cardNumber)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch monthly FAILED topup status for card={} month={} year={}",
                                        cardNumber, month,
                                        year, e);
                        return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly FAILED topup status for card=" + cardNumber)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(YearTopupStatusCardNumber req) {
                String cardNumber = req.getCardNumber();
                Long year = (long) req.getYear();
                log.info("📊 Fetching yearly FAILED topup status for card={} year={}", cardNumber, year);

                validateCard(cardNumber);
                validateYear(year);

                try {
                        List<TopupYearStatusFailed> data = topupStatusByCardRepository
                                        .findYearlyTopupStatusFailedByCard(cardNumber,
                                                        year);
                        List<TopupResponseYearStatusFailed> response = data.stream()
                                        .map(TopupResponseYearStatusFailed::from)
                                        .toList();

                        log.info("✅ Retrieved {} yearly FAILED records for card={} year={}", response.size(),
                                        cardNumber, year);

                        return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly FAILED topup status for card="
                                                        + cardNumber)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch yearly FAILED topup status for card={} year={}", cardNumber, year,
                                        e);
                        return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly FAILED topup status for card=" + cardNumber)
                                        .build();
                }
        }

        private void validateCard(String cardNumber) {
                if (cardNumber == null || cardNumber.isEmpty()) {
                        log.error("❌ Card number is required");
                        throw new IllegalArgumentException("Card number is required");
                }
        }

        private void validateYearMonth(Long year, Integer month) {
                if (year < 1 || year > 9999)
                        throw new IllegalArgumentException("Invalid year");
                if (month < 1 || month > 12)
                        throw new IllegalArgumentException("Invalid month");
        }

        private void validateYear(Long year) {
                if (year == null || year < 1 || year > 9999) {
                        log.error("❌ Invalid year: {}", year);
                        throw new IllegalArgumentException("Invalid year");
                }
        }
}
