package com.sanedge.payment_gateway.service.impl.merchant;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantQueryRepository;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.service.merchant.MerchantCommandService;
import com.sanedge.payment_gateway.utils.ApiKeyGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantCommandImplService implements MerchantCommandService {
    private final UserQueryRepository userQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final MerchantCommandRepository merchantCommandRepository;

    @Override
    public ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req) {
        log.info("🏪 Creating merchant | Name: {}, UserId: {}", req.getName(), req.getUserId());

        try {
            userQueryRepository.findByUserId(req.getUserId())
                    .orElseThrow(() -> {
                        log.error("❌ User not found with id {}", req.getUserId());
                        return new ResourceNotFoundException("User not found");
                    });

            boolean merchantExists = merchantQueryRepository.existsByName(req.getName());
            if (merchantExists) {
                log.error("❌ Merchant name already taken | Name: {}", req.getName());
                throw new IllegalArgumentException("Merchant name already taken");
            }

            String apiKey = ApiKeyGenerator.generateApiKey();
            UUID merchantNo = UUID.randomUUID();

            Merchant merchant = new Merchant();
            merchant.setName(req.getName());
            merchant.setMerchantNo(merchantNo);
            merchant.setUserId(req.getUserId());
            merchant.setApiKey(apiKey);
            merchant.setStatus(Status.PENDING);
            merchant.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Merchant savedMerchant = merchantCommandRepository.save(merchant);

            log.info("✅ Merchant created successfully | Id: {}, ApiKey: {}", savedMerchant.getMerchantId(), apiKey);

            return ApiResponse.<MerchantResponse>builder()
                    .status("success")
                    .data(MerchantResponse.from(savedMerchant))
                    .message("Merchant created successfully")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create merchant | Name: {}, UserId: {}", req.getName(), req.getUserId(), e);
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to create merchant")
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req) {
        log.info("🛠️ Updating merchant | Id: {}", req.getMerchantId());

        try {
            Merchant merchant = merchantQueryRepository.findById(req.getMerchantId())
                    .orElseThrow(() -> {
                        log.error("❌ Merchant not found with id {}", req.getMerchantId());
                        return new ResourceNotFoundException("Merchant not found");
                    });

            merchant.setName(req.getName());
            merchant.setStatus(Status.valueOf(req.getStatus().toUpperCase()));
            merchant.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            if (req.getUserId() != null) {
                userQueryRepository.findByUserId(req.getUserId())
                        .orElseThrow(() -> {
                            log.error("❌ User not found with id {}", req.getUserId());
                            return new ResourceNotFoundException("User not found");
                        });
                merchant.setUserId(req.getUserId());
            }

            Merchant updatedMerchant = merchantCommandRepository.save(merchant);

            log.info("✅ Merchant updated successfully | Id: {}", updatedMerchant.getMerchantId());

            return ApiResponse.<MerchantResponse>builder()
                    .status("success")
                    .data(MerchantResponse.from(updatedMerchant))
                    .message("Merchant updated successfully")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update merchant | Id: {}", req.getMerchantId(), e);
            return ApiResponse.<MerchantResponse>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to update merchant")
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> trashMerchant(Long id) {
        log.info("🗑️ Trashing merchant id={}", id);
        try {
            Merchant merchant = merchantCommandRepository.trashed(id);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("success")
                    .data(MerchantResponseDeleteAt.from(merchant))
                    .message("Merchant trashed successfully")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash merchant id={}", id, e);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("error")
                    .data(null)
                    .message("Merchant trashing failed")
                    .build();
        }
    }

    @Override
    public ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Long id) {
        log.info("♻️ Restoring merchant id={}", id);
        try {
            Merchant merchant = merchantCommandRepository.restore(id);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("success")
                    .data(MerchantResponseDeleteAt.from(merchant))
                    .message("Merchant restored successfully")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore merchant id={}", id, e);
            return ApiResponse.<MerchantResponseDeleteAt>builder()
                    .status("error")
                    .data(null)
                    .message("Merchant restoring failed")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteMerchant(Long id) {
        log.info("🧨 Permanently deleting merchant id={}", id);
        try {
            merchantCommandRepository.deletePermanent(id);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .data(true)
                    .message("Merchant permanently deleted")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete merchant id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .data(false)
                    .message("Merchant deletion failed")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed merchants");
        try {
            merchantCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .data(true)
                    .message("Restored all trashed merchants")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .data(false)
                    .message("Failed to restore all merchants")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed merchants");
        try {
            merchantCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .data(true)
                    .message("Deleted all trashed merchants")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all merchants", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .data(false)
                    .message("Failed to delete all merchants")
                    .build();
        }
    }
}
