package com.sanedge.payment_gateway.repository.withdraw;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;

public class WithdrawQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawQueryRepository queryRepository;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private Long withdrawId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(cardNumber);
        withdraw.setWithdrawAmount(500000L);
        withdraw.setWithdrawTime(new Timestamp(System.currentTimeMillis()));
        withdraw.setStatus(Status.PENDING);

        Withdraw saved = commandRepository.save(withdraw);
        withdrawId = saved.getWithdrawId();
    }

    @Test
    void testFindWithdraws() {
        Page<Withdraw> page = queryRepository.findAllWithdraws("1234", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindActiveWithdraws() {
        Page<Withdraw> page = queryRepository.findActiveWithdraws(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindTrashedWithdraws() {
        commandRepository.trashed(withdrawId);
        Page<Withdraw> page = queryRepository.findTrashedWithdraws(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindWithdrawById() {
        Optional<Withdraw> withdraw = queryRepository.findById(withdrawId);
        assertThat(withdraw).isPresent();
        assertThat(withdraw.get().getWithdrawId()).isEqualTo(withdrawId);
    }

    @Test
    void testFindWithdrawsByCardNumber() {
        Page<Withdraw> page = queryRepository.findAllByCardNumber(cardNumber, "", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }
}