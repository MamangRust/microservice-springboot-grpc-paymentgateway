package com.sanedge.payment_gateway.service.impl.merchant.statsbyid;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearPaymentMethodMerchant;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyPaymentMethod;
import com.sanedge.payment_gateway.models.merchants.MerchantYearlyPaymentMethod;
import com.sanedge.payment_gateway.repository.merchants.statsbyid.MerchantMethodByIdRepository;
import com.sanedge.payment_gateway.service.merchant.stats.method.MerchantMethodByIdService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantMethodByIdImplService implements MerchantMethodByIdService {
        private final MerchantMethodByIdRepository merchantMethodByIdRepository;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethodById(
                        MonthYearPaymentMethodMerchant req) {
                Long merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                log.info("📊 Fetching monthly payment method stats for merchantId={} and year={}", merchantId, year);

                try {
                        validateYear(year);

                        List<MerchantMonthlyPaymentMethod> monthlyMethods = merchantMethodByIdRepository
                                        .findMonthlyPaymentMethodsById(merchantId, year);

                        List<MerchantResponseMonthlyPaymentMethod> response = monthlyMethods.stream()
                                        .map(MerchantResponseMonthlyPaymentMethod::from)
                                        .toList();

                        log.info("✅ Found {} monthly payment method records for merchantId={} in year={}",
                                        response.size(), merchantId, year);

                        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly payment method stats for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly payment method stats for merchantId={} year={}",
                                        merchantId, year, e);
                        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly payment method stats for year=" + year)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethodById(
                        MonthYearPaymentMethodMerchant req) {
                Long merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                log.info("📊 Fetching yearly payment method stats for merchantId={} and year={}", merchantId, year);

                try {
                        validateYear(year);

                        List<MerchantYearlyPaymentMethod> yearlyMethods = merchantMethodByIdRepository
                                        .findYearlyPaymentMethodsById(merchantId, year);

                        List<MerchantResponseYearlyPaymentMethod> response = yearlyMethods.stream()
                                        .map(MerchantResponseYearlyPaymentMethod::from)
                                        .toList();

                        log.info("✅ Found {} yearly payment method records for merchantId={} in year={}",
                                        response.size(), merchantId, year);

                        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly payment method stats for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly payment method stats for merchantId={} year={}", merchantId,
                                        year, e);
                        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly payment method stats for year=" + year)
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
