package com.sanedge.payment_gateway;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.sanedge.payment_gateway.repository.role.RoleCommandRepository;
import com.sanedge.payment_gateway.repository.role.RoleQueryRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.UserRole;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.user_role.UserRoleRepository;
import com.sanedge.payment_gateway.enums.Status;
import java.util.UUID;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    protected RoleCommandRepository roleCommandRepository;

    @Autowired
    protected RoleQueryRepository roleQueryRepository;

    @Autowired
    protected UserCommandRepository userCommandRepository;

    @Autowired
    protected com.sanedge.payment_gateway.repository.user.UserQueryRepository userQueryRepository;

    @Autowired
    protected MerchantCommandRepository merchantCommandRepository;

    @Autowired
    protected com.sanedge.payment_gateway.repository.merchants.MerchantQueryRepository merchantQueryRepository;

    @Autowired
    protected UserRoleRepository userRoleRepository;

    @Autowired
    protected CardCommandRepository cardCommandRepository;

    @Autowired
    protected com.sanedge.payment_gateway.repository.card.CardQueryRepository cardQueryRepository;

    @Autowired
    protected SaldoCommandRepository saldoCommandRepository;

    @Autowired
    protected com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository saldoQueryRepository;

    @Autowired
    protected jakarta.persistence.EntityManager entityManager;

    protected User adminUser;
    protected User regularUser;
    protected Card adminCard;
    protected Card regularCard;
    protected Merchant adminMerchant;
    protected Saldo adminSaldo;
    protected Saldo regularSaldo;

    @BeforeEach
    @Transactional
    void baseSetup() {
        entityManager.flush();

        ensureRoles();
        this.adminUser = ensureUsers();
        this.adminMerchant = ensureMerchant(this.adminUser);
        this.adminCard = ensureCard(this.adminUser, "1111222233334444");
        this.adminSaldo = ensureSaldo(this.adminCard);
        
        this.regularCard = ensureCard(this.regularUser, "5555666677778888");
        this.regularSaldo = ensureSaldo(this.regularCard);
        
        entityManager.flush();
        entityManager.clear();
    }

    protected void ensureRoles() {
        if (roleQueryRepository.findByRoleName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRoleName("ROLE_ADMIN");
            roleCommandRepository.save(adminRole);
        }

        if (roleQueryRepository.findByRoleName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setRoleName("ROLE_USER");
            roleCommandRepository.save(userRole);
        }

        entityManager.flush();
    }

    protected User ensureUsers() {
        User admin = userQueryRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setEmail("admin@example.com");
            admin.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2"); // "password"
            admin = userCommandRepository.save(admin);

            Role adminRole = roleQueryRepository.findByRoleName("ROLE_ADMIN").orElseThrow();
            UserRole adminUserRole = new UserRole();
            adminUserRole.setUser(admin);
            adminUserRole.setRole(adminRole);
            userRoleRepository.save(adminUserRole);
        }

        User user = userQueryRepository.findByUsername("user").orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername("user");
            user.setFirstname("Regular");
            user.setLastname("User");
            user.setEmail("user@example.com");
            user.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2"); // "password"
            user = userCommandRepository.save(user);

            Role userRole = roleQueryRepository.findByRoleName("ROLE_USER").orElseThrow();
            UserRole userUserRole = new UserRole();
            userUserRole.setUser(user);
            userUserRole.setRole(userRole);
            userRoleRepository.save(userUserRole);
        }

        this.regularUser = user;

        entityManager.flush();

        return admin;
    }

    protected Merchant ensureMerchant(User user) {
        List<Merchant> merchants = merchantQueryRepository.findByUserId(user.getUserId());
        if (!merchants.isEmpty()) {
            return merchants.get(0);
        }
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Default Merchant");
        merchant.setApiKey("base-test-api-key-" + user.getUserId());
        merchant.setUserId(user.getUserId());
        merchant.setStatus(Status.SUCCESS);
        Merchant saved = merchantCommandRepository.save(merchant);
        entityManager.flush();
        return saved;
    }

    protected Card ensureCard(User user, String cardNumber) {
        return cardQueryRepository.findCardByUserId(user.getUserId()).orElseGet(() -> {
            Card card = new Card();
            card.setUserId(user.getUserId());
            card.setCardNumber(cardNumber);
            card.setCardType("CREDIT");
            card.setExpireDate(new java.sql.Date(System.currentTimeMillis() + 31536000000L)); // +1 year
            card.setCvv("123");
            card.setCardProvider("VISA");
            Card saved = cardCommandRepository.save(card);
            entityManager.flush();
            return saved;
        });
    }

    protected Saldo ensureSaldo(Card card) {
        return saldoQueryRepository.findByCardNumber(card.getCardNumber()).orElseGet(() -> {
            Saldo saldo = new Saldo();
            saldo.setCardNumber(card.getCardNumber());
            saldo.setTotalBalance(1000000L);
            Saldo saved = saldoCommandRepository.save(saldo);
            entityManager.flush();
            return saved;
        });
    }
}