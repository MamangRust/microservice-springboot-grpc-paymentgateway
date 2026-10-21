package com.sanedge.payment_gateway.service.transfer.stats.amount;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.MonthYearCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferYearAmountResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransferAmountByCardService amountByCardService;

    @Autowired
    private TransferCommandRepository commandRepository;

    private String transferFrom = "1111222233334444";
    private String transferTo = "5555666677778888";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Sender t1
        Transfer t1 = new Transfer();
        t1.setTransferNo(UUID.randomUUID());
        t1.setTransferFrom(transferFrom);
        t1.setTransferTo(transferTo);
        t1.setTransferAmount(500000L);
        t1.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Receiver t2 (different sender)
        Transfer t2 = new Transfer();
        t2.setTransferNo(UUID.randomUUID());
        t2.setTransferFrom("6543210987654321");
        t2.setTransferTo(transferTo);
        t2.setTransferAmount(300000L);
        t2.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 20, 14, 0)));
        t2.setStatus(Status.SUCCESS);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthlyAmountsBySender() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearCardNumber req = new MonthYearCardNumber();
        req.setCardNumber(transferFrom);
        req.setYear(year.intValue());

        ApiResponse<List<TransferMonthAmountResponse>> response = amountByCardService.findMonthlyAmountsBySender(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(12);

        TransferMonthAmountResponse april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyAmountsBySender() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearCardNumber req = new MonthYearCardNumber();
        req.setCardNumber(transferFrom);
        req.setYear(year.intValue());

        ApiResponse<List<TransferYearAmountResponse>> response = amountByCardService.findYearlyAmountsBySender(req);
        assertThat(response.getStatus()).isEqualTo("success");

        TransferYearAmountResponse current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindMonthlyAmountsByReceiver() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearCardNumber req = new MonthYearCardNumber();
        req.setCardNumber(transferTo);
        req.setYear(year.intValue());

        ApiResponse<List<TransferMonthAmountResponse>> response = amountByCardService.findMonthlyAmountsByReceiver(req);
        assertThat(response.getStatus()).isEqualTo("success");

        TransferMonthAmountResponse april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(800000L);
    }

    @Test
    void testFindYearlyAmountsByReceiver() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearCardNumber req = new MonthYearCardNumber();
        req.setCardNumber(transferTo);
        req.setYear(year.intValue());

        ApiResponse<List<TransferYearAmountResponse>> response = amountByCardService.findYearlyAmountsByReceiver(req);
        assertThat(response.getStatus()).isEqualTo("success");

        TransferYearAmountResponse current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(800000L);
    }
}