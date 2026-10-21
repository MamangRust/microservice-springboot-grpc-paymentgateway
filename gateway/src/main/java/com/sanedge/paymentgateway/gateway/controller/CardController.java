package com.sanedge.paymentgateway.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.gateway.dto.requests.card.CreateCardRequest;
import com.sanedge.paymentgateway.gateway.dto.requests.card.FindAllCards;
import com.sanedge.paymentgateway.gateway.dto.requests.card.UpdateCardRequest;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboard;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboardCard;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.card.CardCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardDashboardServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardQueryServiceGrpc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@PreAuthorize("isAuthenticated()")
public class CardController {

    private final CardQueryServiceGrpc.CardQueryServiceBlockingStub cardQueryStub;
    private final CardCommandServiceGrpc.CardCommandServiceBlockingStub cardCommandStub;
    private final CardDashboardServiceGrpc.CardDashboardServiceBlockingStub cardDashboardStub;

    // ---- CRUD ----

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<CardResponse>>> findCards(@Valid @ModelAttribute FindAllCards req) {
        var resp = cardQueryStub.findAllCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CardResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cards(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<CardResponseDeleteAt>>> findActiveCards(
            @Valid @ModelAttribute FindAllCards req) {
        var resp = cardQueryStub.findByActiveCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<CardResponseDeleteAt>>> findTrashedCards(
            @Valid @ModelAttribute FindAllCards req) {
        var resp = cardQueryStub.findByTrashedCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCard(@PathVariable Long id) {
        var resp = cardQueryStub.findByIdCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build());
    }

    @GetMapping("/by-user/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCardByUser(@PathVariable Long user_id) {
        var resp = cardQueryStub.findByUserIdCard(com.sanedge.paymentgateway.pb.card.FindByUserIdCardRequest.newBuilder()
                .setUserId(user_id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build());
    }

    @GetMapping("/by-number/{card_number}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCardByNumber(@PathVariable String card_number) {
        var resp = cardQueryStub.findByCardNumber(com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                .setCardNumber(card_number)
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponse>> createCard(@Valid @RequestBody CreateCardRequest request) {
        var resp = cardCommandStub.createCard(com.sanedge.paymentgateway.pb.card.CreateCardRequest.newBuilder()
                .setUserId(nvl(request.getUserId()))
                .setCardType(nvl(request.getCardType()))
                .setExpireDate(ProtoMapper.toTimestamp(request.getExpireDate()))
                .setCvv(nvl(request.getCvv()))
                .setCardProvider(nvl(request.getCardProvider()))
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(@Valid @RequestBody UpdateCardRequest request) {
        var resp = cardCommandStub.updateCard(com.sanedge.paymentgateway.pb.card.UpdateCardRequest.newBuilder()
                .setCardId(nvl(request.getCardId()))
                .setUserId(nvl(request.getUserId()))
                .setCardType(nvl(request.getCardType()))
                .setExpireDate(ProtoMapper.toTimestamp(request.getExpireDate()))
                .setCvv(nvl(request.getCvv()))
                .setCardProvider(nvl(request.getCardProvider()))
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponseDeleteAt>> trashCard(@PathVariable Long id) {
        var resp = cardCommandStub.trashedCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponseDeleteAt>> restoreCard(@PathVariable Long id) {
        var resp = cardCommandStub.restoreCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<CardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCard(@PathVariable Long id) {
        var resp = cardCommandStub.deleteCardPermanent(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllCards() {
        var resp = cardCommandStub.restoreAllCard(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCards() {
        var resp = cardCommandStub.deleteAllCardPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    // ---- dashboard ----

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardDashboard>> findCardDashboard() {
        var resp = cardDashboardStub.dashboardCard(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<CardDashboard>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDashboard(resp.getData()))
                .build());
    }

    @GetMapping("/dashboard/{card_number}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<CardDashboardCard>> findCardDashboardByCardNumber(
            @PathVariable String card_number) {
        var resp = cardDashboardStub
                .dashboardCardNumber(com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                        .setCardNumber(card_number)
                        .build());
        return ResponseEntity.ok(ApiResponse.<CardDashboardCard>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDashboardCard(resp.getData()))
                .build());
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static int nvl(Long value) {
        return value == null ? 0 : value.intValue();
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
