package com.sanedge.payment_gateway.service.withdraw;

import com.sanedge.payment_gateway.domain.requests.withdraws.CreateWithdrawRequest;
import com.sanedge.payment_gateway.domain.requests.withdraws.UpdateWithdrawRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;

public interface WithdrawCommandService {

    ApiResponse<WithdrawResponse> create(CreateWithdrawRequest req);

    ApiResponse<WithdrawResponse> update(UpdateWithdrawRequest req);

    ApiResponse<WithdrawResponseDeleteAt> trashed(Long withdrawId);

    ApiResponse<WithdrawResponseDeleteAt> restore(Long withdrawId);

    ApiResponse<Boolean> deletePermanent(Long withdrawId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
