package com.sanedge.payment_gateway.service.transfer;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transfers.FindAllTransfers;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransferQueryService queryService;

    @Autowired
    private TransferCommandRepository commandRepository;

    private Long transferId;
    private String transferFrom = "1111222233334444";
    private String transferTo = "5555666677778888";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(transferFrom);
        transfer.setTransferTo(transferTo);
        transfer.setTransferAmount(500000L);
        transfer.setTransferTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        Transfer saved = commandRepository.save(transfer);
        transferId = saved.getTransferId();
    }

    @Test
    void testFindAll() {
        FindAllTransfers req = new FindAllTransfers();
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<TransferResponse>> response = queryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindById() {
        ApiResponse<TransferResponse> response = queryService.findById(transferId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getId()).isEqualTo(transferId);
    }

    @Test
    void testFindByActive() {
        FindAllTransfers req = new FindAllTransfers();
        ApiResponsePagination<List<TransferResponseDeleteAt>> response = queryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByTrashed() {
        commandRepository.trashed(transferId);
        FindAllTransfers req = new FindAllTransfers();
        ApiResponsePagination<List<TransferResponseDeleteAt>> response = queryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByTransferFrom() {
        ApiResponse<List<TransferResponse>> response = queryService.findByTransferFrom(transferFrom);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByTransferTo() {
        ApiResponse<List<TransferResponse>> response = queryService.findByTransferTo(transferTo);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }
}