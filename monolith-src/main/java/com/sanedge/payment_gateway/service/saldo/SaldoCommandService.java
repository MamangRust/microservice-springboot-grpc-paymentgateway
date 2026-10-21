package com.sanedge.payment_gateway.service.saldo;

import com.sanedge.payment_gateway.domain.requests.saldo.CreateSaldoRequest;
import com.sanedge.payment_gateway.domain.requests.saldo.UpdateSaldoRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;

public interface SaldoCommandService {

    ApiResponse<SaldoResponse> create(CreateSaldoRequest request);

    ApiResponse<SaldoResponse> update(UpdateSaldoRequest request);

    ApiResponse<SaldoResponseDeleteAt> trash(Long id);

    ApiResponse<SaldoResponseDeleteAt> restore(Long id);

    ApiResponse<Boolean> delete(Long id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
