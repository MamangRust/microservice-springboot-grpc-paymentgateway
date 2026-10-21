package com.sanedge.payment_gateway.service.topup;

import com.sanedge.payment_gateway.domain.requests.topup.CreateTopupRequest;
import com.sanedge.payment_gateway.domain.requests.topup.UpdateTopupRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;

public interface TopupCommandService {

    ApiResponse<TopupResponse> create(CreateTopupRequest req);

    ApiResponse<TopupResponse> update(UpdateTopupRequest req);

    ApiResponse<TopupResponseDeleteAt> trashed(Long topupId);

    ApiResponse<TopupResponseDeleteAt> restore(Long topupId);

    ApiResponse<Boolean> deletePermanent(Long topupId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}