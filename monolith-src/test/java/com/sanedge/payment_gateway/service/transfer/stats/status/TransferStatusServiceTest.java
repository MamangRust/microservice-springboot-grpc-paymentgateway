package com.sanedge.payment_gateway.service.transfer.stats.status;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transfers.stats.MonthStatusTransfer;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusSuccess;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferStatusServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransferStatusService statusService;

    @Autowired
    private TransferCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success t1
        Transfer t1 = new Transfer();
        t1.setTransferNo(UUID.randomUUID());
        t1.setTransferFrom("1111222233334444");
        t1.setTransferTo("5555666677778888");
        t1.setTransferAmount(500000L);
        t1.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Failed t2
        Transfer t2 = new Transfer();
        t2.setTransferNo(UUID.randomUUID());
        t2.setTransferFrom("1111222233334444");
        t2.setTransferTo("5555666677778888");
        t2.setTransferAmount(300000L);
        t2.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        t2.setStatus(Status.FAILED);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusTransfer req = new MonthStatusTransfer();
        req.setYear(year.intValue());
        req.setMonth(4);

        ApiResponse<List<TransferResponseMonthStatusSuccess>> response = statusService.findMonthStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransferResponseMonthStatusSuccess april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<TransferResponseYearStatusSuccess>> response = statusService.findYearlyStatusSuccess(year);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransferResponseYearStatusSuccess current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusTransfer req = new MonthStatusTransfer();
        req.setYear(year.intValue());
        req.setMonth(5);

        ApiResponse<List<TransferResponseMonthStatusFailed>> response = statusService.findMonthStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransferResponseMonthStatusFailed may = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<TransferResponseYearStatusFailed>> response = statusService.findYearlyStatusFailed(year);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransferResponseYearStatusFailed current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(1L);
    }
}