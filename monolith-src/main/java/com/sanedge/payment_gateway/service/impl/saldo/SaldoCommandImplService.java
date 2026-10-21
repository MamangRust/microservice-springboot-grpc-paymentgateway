package com.sanedge.payment_gateway.service.impl.saldo;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.saldo.CreateSaldoRequest;
import com.sanedge.payment_gateway.domain.requests.saldo.UpdateSaldoRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.service.saldo.SaldoCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaldoCommandImplService implements SaldoCommandService {
    private final CardQueryRepository cardQueryRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final SaldoQueryRepository saldoQueryRepository;

    @Override
    public ApiResponse<SaldoResponse> create(CreateSaldoRequest request) {
        try {
            log.info("Creating saldo for card_number={}", request.getCardNumber());

            cardQueryRepository.findCardByCardNumber(request.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("Card {} not found", request.getCardNumber());
                        return new ResourceNotFoundException("Card not found");
                    });

            Saldo saldo = new Saldo();
            saldo.setCardNumber(request.getCardNumber());
            saldo.setTotalBalance(request.getTotalBalance());
            saldo.setWithdrawAmount(null);
            saldo.setWithdrawTime(null);

            Saldo savedSaldo = saldoCommandRepository.save(saldo);

            log.info("Saldo created successfully with id={} for card={}", savedSaldo.getSaldoId(),
                    request.getCardNumber());

            return ApiResponse.<SaldoResponse>builder()
                    .status("success")
                    .message("Create saldo success")
                    .data(SaldoResponse.from(savedSaldo))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to create saldo for card_number={}", request.getCardNumber(), e);
            return ApiResponse.<SaldoResponse>builder()
                    .status("error")
                    .message("Failed to create saldo")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<SaldoResponse> update(UpdateSaldoRequest request) {
        try {
            log.info("Updating saldo id={} for card={}", request.getSaldoId(), request.getCardNumber());

            if (request.getSaldoId() == null) {
                log.error("saldo_id is required");
                throw new IllegalArgumentException("saldo_id is required");
            }

            cardQueryRepository.findCardByCardNumber(request.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("Card {} not found during update", request.getCardNumber());
                        return new ResourceNotFoundException("Card not found");
                    });

            Saldo saldo = saldoQueryRepository.findById(request.getSaldoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Saldo not found"));

            saldo.setCardNumber(request.getCardNumber());
            saldo.setTotalBalance(request.getTotalBalance());

            Saldo updateSaldo = saldoCommandRepository.save(saldo);

            log.info("Saldo updated successfully with id={} for card={}", updateSaldo.getSaldoId(),
                    request.getCardNumber());

            return ApiResponse.<SaldoResponse>builder()
                    .status("success")
                    .message("Update saldo success")
                    .data(SaldoResponse.from(updateSaldo))
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to update saldo id={} for card={}", request.getSaldoId(), request.getCardNumber(), e);
            return ApiResponse.<SaldoResponse>builder()
                    .status("error")
                    .message("Failed to update saldo")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<SaldoResponseDeleteAt> trash(Long id) {
        try {
            log.info("🗑️ Trashing saldo id={}", id);
            Saldo saldo = saldoCommandRepository.trashed(id);
            return ApiResponse.<SaldoResponseDeleteAt>builder()
                    .status("success")
                    .message("Trash saldo success")
                    .data(SaldoResponseDeleteAt.from(saldo))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash saldo id={}", id, e);
            return ApiResponse.<SaldoResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash saldo")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<SaldoResponseDeleteAt> restore(Long id) {
        try {
            log.info("♻️ Restoring saldo id={}", id);
            Saldo saldo = saldoCommandRepository.restore(id);
            return ApiResponse.<SaldoResponseDeleteAt>builder()
                    .status("success")
                    .message("Restore saldo success")
                    .data(SaldoResponseDeleteAt.from(saldo))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore saldo id={}", id, e);
            return ApiResponse.<SaldoResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore saldo")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> delete(Long id) {
        try {
            log.info("🧨 Permanently deleting saldo id={}", id);
            saldoCommandRepository.deletePermanent(id);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Delete saldo success")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete saldo id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete saldo")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        try {
            log.info("🔄 Restoring ALL trashed saldos");
            saldoCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Restore all saldo success")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all saldos", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all saldo")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        try {
            log.info("💣 Permanently deleting ALL trashed saldos");
            saldoCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Delete all saldo success")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all saldos", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all saldo")
                    .data(false)
                    .build();
        }
    }
}