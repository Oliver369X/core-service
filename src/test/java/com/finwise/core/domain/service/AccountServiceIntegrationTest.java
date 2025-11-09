package com.finwise.core.domain.service;

import com.finwise.core.domain.model.Account;
import com.finwise.core.domain.model.AccountType;
import com.finwise.core.support.MySQLContainerSupport;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceIntegrationTest extends MySQLContainerSupport {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM transactions");
        jdbcTemplate.execute("DELETE FROM goals");
        jdbcTemplate.execute("DELETE FROM budgets");
        jdbcTemplate.execute("DELETE FROM accounts");
    }

    @Test
    void createAccountPersistsAndReturnsData() {
        Account account = accountService.createAccount(
                "user-123",
                "Cuenta Ahorros",
                AccountType.SAVINGS,
                new BigDecimal("1500.00"),
                "USD"
        );

        assertThat(account.getId()).isNotNull();
        assertThat(account.getBalance()).isEqualByComparingTo("1500.00");
        assertThat(accountService.findById(account.getId()).getName()).isEqualTo("Cuenta Ahorros");
    }

    @Test
    void updateBalanceReflectsInPersistence() {
        Account account = accountService.createAccount(
                "user-999",
                "Cuenta Corriente",
                AccountType.CHECKING,
                new BigDecimal("100.00"),
                "USD"
        );

        accountService.updateBalance(account.getId(), new BigDecimal("450.25"));

        Account updated = accountService.findById(account.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo("450.25");
    }
}
