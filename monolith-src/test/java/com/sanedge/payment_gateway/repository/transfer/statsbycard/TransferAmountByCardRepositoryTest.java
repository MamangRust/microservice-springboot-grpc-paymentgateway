package com.sanedge.payment_gateway.repository.transfer.statsbycard;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.models.transfer.TransferMonthAmount;
import com.sanedge.payment_gateway.models.transfer.TransferYearAmount;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferAmountByCardRepository amountByCardRepository;

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
    void testFindMonthlyTransferAmountsBySender() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthAmount> results = amountByCardRepository.findMonthlyTransferAmountsBySender(transferFrom, year);

        assertThat(results).hasSize(12);

        TransferMonthAmount april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyTransferAmountsBySender() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearAmount> results = amountByCardRepository.findYearlyTransferAmountsBySender(transferFrom, year);

        assertThat(results).isNotEmpty();

        TransferYearAmount current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindMonthlyTransferAmountsByReceiver() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthAmount> results = amountByCardRepository.findMonthlyTransferAmountsByReceiver(transferTo, year);

        assertThat(results).hasSize(12);

        TransferMonthAmount april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(800000L); // 500000 + 300000;
    }

    @Test
    void testFindYearlyTransferAmountsByReceiver() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearAmount> results = amountByCardRepository.findYearlyTransferAmountsByReceiver(transferTo, year);

        assertThat(results).isNotEmpty();

        TransferYearAmount current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(800000L);
    }
}