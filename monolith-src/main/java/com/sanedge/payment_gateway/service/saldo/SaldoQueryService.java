package com.sanedge.payment_gateway.service.saldo;

import com.sanedge.payment_gateway.domain.requests.saldo.FindAllSaldos;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;

import java.util.List;

public interface SaldoQueryService {

    ApiResponsePagination<List<SaldoResponse>> findAll(FindAllSaldos request);

    ApiResponsePagination<List<SaldoResponseDeleteAt>> findActive(FindAllSaldos request);

    ApiResponsePagination<List<SaldoResponseDeleteAt>> findTrashed(FindAllSaldos request);

    ApiResponse<SaldoResponse> findByCard(String cardNumber);

    ApiResponse<SaldoResponse> findById(Long id);
}
