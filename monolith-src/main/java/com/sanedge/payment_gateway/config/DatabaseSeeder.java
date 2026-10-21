package com.sanedge.payment_gateway.config;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantQueryRepository;
import com.sanedge.payment_gateway.repository.role.RoleCommandRepository;
import com.sanedge.payment_gateway.repository.role.RoleQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserQueryRepository userQueryRepository;
    private final RoleQueryRepository roleQueryRepository;
    private final CardQueryRepository cardQueryRepository;
    private final SaldoQueryRepository saldoQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;

    private final UserCommandRepository userCommandRepository;
    private final RoleCommandRepository roleCommandRepository;
    private final CardCommandRepository cardCommandRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final MerchantCommandRepository merchantCommandRepository;
    private final TopupCommandRepository topupCommandRepository;
    private final TransactionCommandRepository transactionCommandRepository;
    private final TransferCommandRepository transferCommandRepository;
    private final WithdrawCommandRepository withdrawCommandRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting database seeding...");

        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role userRole = createRoleIfNotFound("ROLE_USER");
        Role staffRole = createRoleIfNotFound("ROLE_STAFF");

        User admin = createUserIfNotFound("admin", "admin@sanedge.com", "Admin", "System",
                new HashSet<>(List.of(adminRole, staffRole)));
        User user1 = createUserIfNotFound("user1", "user1@sanedge.com", "John", "Doe",
                new HashSet<>(List.of(userRole)));
        User user2 = createUserIfNotFound("user2", "user2@sanedge.com", "Jane", "Smith",
                new HashSet<>(List.of(userRole)));
        User staff = createUserIfNotFound("staff", "staff@sanedge.com", "Staff", "Member",
                new HashSet<>(List.of(staffRole)));

        Merchant merchant = createMerchantIfNotFound("Toko Elektronik", admin);

        Card cardAdmin = createCardIfNotFound("1111222233334444", admin, "CREDIT", "VISA");
        Card cardUser1 = createCardIfNotFound("5555666677778888", user1, "DEBIT", "MASTERCARD");
        Card cardUser2 = createCardIfNotFound("9999000011112222", user2, "DEBIT", "VISA");

        createSaldoIfNotFound(cardAdmin.getCardNumber(), 5000000L);
        createSaldoIfNotFound(cardUser1.getCardNumber(), 1000000L);
        createSaldoIfNotFound(cardUser2.getCardNumber(), 500000L);

        createTopup(cardUser1.getCardNumber(), 500000L);
        createTransfer(cardUser1.getCardNumber(), cardUser2.getCardNumber(), 100000L);
        createWithdraw(cardAdmin.getCardNumber(), 500000L);
        createTransaction(cardUser1.getCardNumber(), merchant.getMerchantId(), 250000L);

        log.info("Database seeding completed.");
    }

    private Role createRoleIfNotFound(String roleName) {
        return roleQueryRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    return roleCommandRepository.save(newRole);
                });
    }

    private User createUserIfNotFound(String username, String email, String fname, String lname, Set<Role> roles) {
        return userQueryRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setFirstname(fname);
                    newUser.setLastname(lname);
                    newUser.setPassword(passwordEncoder.encode("password123"));
                    newUser.setRoles(roles);
                    return userCommandRepository.save(newUser);
                });
    }

    private Merchant createMerchantIfNotFound(String name, User owner) {
        return merchantQueryRepository.findByName(name)
                .orElseGet(() -> {
                    Merchant newMerchant = new Merchant();
                    newMerchant.setName(name);
                    newMerchant.setMerchantNo(UUID.randomUUID());
                    newMerchant.setApiKey(UUID.randomUUID().toString());
                    newMerchant.setUserId(owner.getUserId());
                    newMerchant.setStatus(Status.SUCCESS);
                    return merchantCommandRepository.save(newMerchant);
                });
    }

    private Card createCardIfNotFound(String cardNumber, User user, String type, String provider) {
        return cardQueryRepository.findCardByCardNumber(cardNumber)
                .orElseGet(() -> {
                    Card newCard = new Card();
                    newCard.setCardNumber(cardNumber);
                    newCard.setUserId(user.getUserId());
                    newCard.setCardType(type);
                    newCard.setCardProvider(provider);
                    newCard.setCvv("123");
                    newCard.setExpireDate(Date.valueOf(LocalDate.now().plusYears(2)));
                    return cardCommandRepository.save(newCard);
                });
    }

    private void createSaldoIfNotFound(String cardNumber, Long balance) {
        saldoQueryRepository.findByCardNumber(cardNumber)
                .orElseGet(() -> {
                    Saldo newSaldo = new Saldo();
                    newSaldo.setCardNumber(cardNumber);
                    newSaldo.setTotalBalance(balance);
                    newSaldo.setWithdrawAmount(0L);
                    return saldoCommandRepository.save(newSaldo);
                });
    }

    private void createTopup(String cardNumber, Long amount) {
        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber(cardNumber);
        topup.setTopupAmount(amount);
        topup.setTopupMethod("BANK_TRANSFER");
        topup.setTopupTime(Timestamp.from(Instant.now()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);
    }

    private void createTransfer(String fromCard, String toCard, Long amount) {
        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(fromCard);
        transfer.setTransferTo(toCard);
        transfer.setTransferAmount(amount);
        transfer.setTransferTime(Timestamp.from(Instant.now()));
        transfer.setStatus(Status.SUCCESS);
        transferCommandRepository.save(transfer);
    }

    private void createWithdraw(String cardNumber, Long amount) {
        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(cardNumber);
        withdraw.setWithdrawAmount(amount);
        withdraw.setWithdrawTime(Timestamp.from(Instant.now()));
        withdraw.setStatus(Status.SUCCESS);
        withdrawCommandRepository.save(withdraw);
    }

    private void createTransaction(String cardNumber, Long merchantId, Long amount) {
        Transaction trx = new Transaction();
        trx.setTransactionNo(UUID.randomUUID());
        trx.setCardNumber(cardNumber);
        trx.setMerchantId(merchantId);
        trx.setAmount(amount);
        trx.setPaymentMethod("CARD");
        trx.setTransactionTime(Timestamp.from(Instant.now()));
        trx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(trx);
    }
}