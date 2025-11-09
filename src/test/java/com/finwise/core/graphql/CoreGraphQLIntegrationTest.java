package com.finwise.core.graphql;

import com.finwise.core.domain.model.Account;
import com.finwise.core.domain.model.AccountType;
import com.finwise.core.domain.model.Transaction;
import com.finwise.core.domain.model.TransactionType;
import com.finwise.core.domain.repository.AccountRepository;
import com.finwise.core.graphql.dto.CreateAccountInput;
import com.finwise.core.graphql.dto.RegisterTransactionInput;
import com.finwise.core.support.MySQLContainerSupport;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CoreGraphQLIntegrationTest extends MySQLContainerSupport {

    @Autowired
    private CoreGraphQLController controller;

    @Autowired
    private AccountRepository accountRepository;

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
    void createAccountAndRegisterTransactionFlow() {
        CreateAccountInput createAccountInput = new CreateAccountInput(
                "user-graph",
                "Cuenta GraphQL",
                AccountType.SAVINGS,
                new BigDecimal("500.00"),
                "USD"
        );

        Account account = controller.createAccount(createAccountInput);
        assertThat(account.getId()).isNotNull();
        assertThat(account.getBalance()).isEqualByComparingTo("500.00");

        RegisterTransactionInput registerTransactionInput = new RegisterTransactionInput(
                account.getId(),
                TransactionType.EXPENSE,
                new BigDecimal("125.50"),
                "Pago de servicios",
                OffsetDateTime.now()
        );

        Transaction transaction = controller.registerTransaction(registerTransactionInput);
        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getAmount()).isEqualByComparingTo("125.50");

        Account reloaded = controller.accountById(account.getId());
        assertThat(reloaded.getBalance()).isEqualByComparingTo("374.50");

        assertThat(controller.transactions(account, Optional.empty(), Optional.empty(), Optional.empty()))
                .hasSize(1)
                .first()
                .extracting(Transaction::getDescription)
                .isEqualTo("Pago de servicios");
    }
}
