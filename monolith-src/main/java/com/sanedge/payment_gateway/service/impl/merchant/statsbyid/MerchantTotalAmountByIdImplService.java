package com.sanedge.payment_gateway.service.impl.merchant.statsbyid;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearTotalAmountMerchant;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyTotalAmount;
import com.sanedge.payment_gateway.models.merchants.MerchantYearlyTotalAmount;
import com.sanedge.payment_gateway.repository.merchants.statsbyid.MerchantTotalAmountByIdRepository;
import com.sanedge.payment_gateway.service.merchant.stats.totalamount.MerchantTotalAmountByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantTotalAmountByIdImplService implements MerchantTotalAmountByIdService {
        private final MerchantTotalAmountByIdRepository merchantTotalAmountByIdRepository;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountById(
                        MonthYearTotalAmountMerchant req) {
                Long merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                log.info("📊 Fetching monthly total merchant amounts for merchantId={} and year={}", merchantId, year);

                try {
                        validateYear(year);

                        LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);

                        List<MerchantMonthlyTotalAmount> monthlyTotals = merchantTotalAmountByIdRepository
                                        .findMonthlyTotalAmountById(merchantId, yearDate);

                        List<MerchantResponseMonthlyTotalAmount> response = monthlyTotals.stream()
                                        .map(MerchantResponseMonthlyTotalAmount::from)
                                        .toList();

                        log.info("✅ Found {} monthly total amount records for merchantId={} in year={}",
                                        response.size(), merchantId, year);

                        return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly total merchant amounts for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly total merchant amounts for merchantId={} year={}",
                                        merchantId, year, e);
                        return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly total merchant amounts for year=" + year)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountById(
                        MonthYearTotalAmountMerchant req) {
                Long merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                log.info("📊 Fetching yearly total merchant amounts for merchantId={} and year={}", merchantId, year);

                try {
                        validateYear(year);

                        Long prevYear = year - 1L;

                        List<MerchantYearlyTotalAmount> yearlyTotals = merchantTotalAmountByIdRepository
                                        .findYearlyTotalAmountById(merchantId, year, prevYear);

                        List<MerchantResponseYearlyTotalAmount> response = yearlyTotals.stream()
                                        .map(MerchantResponseYearlyTotalAmount::from)
                                        .toList();

                        log.info("✅ Found {} yearly total amount records for merchantId={} in year={}", response.size(),
                                        merchantId, year);

                        return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly total merchant amounts for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly total merchant amounts for merchantId={} year={}",
                                        merchantId, year, e);
                        return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly total merchant amounts for year=" + year)
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
