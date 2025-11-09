package com.finwise.core.graphql;

import com.finwise.core.domain.model.Account;
import com.finwise.core.domain.model.AccountType;
import com.finwise.core.domain.model.Budget;
import com.finwise.core.domain.model.Goal;
import com.finwise.core.domain.model.Transaction;
import com.finwise.core.domain.model.TransactionType;
import com.finwise.core.domain.service.AccountService;
import com.finwise.core.domain.service.BudgetService;
import com.finwise.core.domain.service.GoalService;
import com.finwise.core.domain.service.TransactionService;
import com.finwise.core.graphql.dto.CreateAccountInput;
import com.finwise.core.graphql.dto.CreateBudgetInput;
import com.finwise.core.graphql.dto.CreateGoalInput;
import com.finwise.core.graphql.dto.RegisterTransactionInput;
import com.finwise.core.graphql.dto.UpdateAccountInput;
import com.finwise.core.graphql.dto.UpdateBudgetInput;
import com.finwise.core.graphql.dto.UpdateGoalProgressInput;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CoreGraphQLController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final GoalService goalService;

    public CoreGraphQLController(AccountService accountService,
                                 TransactionService transactionService,
                                 BudgetService budgetService,
                                 GoalService goalService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.goalService = goalService;
    }

    @QueryMapping
    public List<Account> accountsByUser(@Argument String userId) {
        return accountService.findByUserId(userId);
    }

    @QueryMapping
    public Account accountById(@Argument Long id) {
        return accountService.findById(id);
    }

    @QueryMapping
    public List<Budget> budgetsByUser(@Argument String userId, @Argument Optional<Boolean> activeOnly) {
        boolean active = activeOnly.orElse(true);
        if (active) {
            return budgetService.findActiveBudgets(userId, LocalDate.now());
        }
        return budgetService.findAllBudgets(userId);
    }

    @QueryMapping
    public List<Goal> goalsByUser(@Argument String userId) {
        return goalService.findByUser(userId);
    }

    @MutationMapping
    public Account createAccount(@Argument CreateAccountInput input) {
        BigDecimal balance = input.balance() != null ? input.balance() : BigDecimal.ZERO;
        String currency = input.currency() != null ? input.currency() : "USD";
        AccountType type = input.type() != null ? input.type() : AccountType.SAVINGS;
        return accountService.createAccount(input.userId(), input.name(), type, balance, currency);
    }

    @MutationMapping
    public Account updateAccount(@Argument UpdateAccountInput input) {
        return accountService.updateAccount(input.id(), input.name(), input.type(), input.currency());
    }

    @MutationMapping
    public Boolean deleteAccount(@Argument Long id) {
        accountService.deleteAccount(id);
        return Boolean.TRUE;
    }

    @MutationMapping
    public Transaction registerTransaction(@Argument RegisterTransactionInput input) {
        OffsetDateTime occurredAt = input.occurredAt();
        return transactionService.registerTransaction(
                input.accountId(),
                Optional.ofNullable(input.type()).orElse(TransactionType.EXPENSE),
                input.amount(),
                input.description(),
                occurredAt
        );
    }

    @MutationMapping
    public Budget createBudget(@Argument CreateBudgetInput input) {
        return budgetService.createBudget(
                input.userId(),
                input.category(),
                input.limitAmount(),
                input.periodStart(),
                input.periodEnd()
        );
    }

    @MutationMapping
    public Budget updateBudget(@Argument UpdateBudgetInput input) {
        return budgetService.updateBudget(input.id(), input.limitAmount(), input.periodStart(), input.periodEnd());
    }

    @MutationMapping
    public Boolean deleteBudget(@Argument Long id) {
        budgetService.deleteBudget(id);
        return Boolean.TRUE;
    }

    @MutationMapping
    public Goal createGoal(@Argument CreateGoalInput input) {
        return goalService.createGoal(
                input.userId(),
                input.name(),
                input.targetAmount(),
                Optional.ofNullable(input.currentAmount()).orElse(BigDecimal.ZERO),
                input.targetDate()
        );
    }

    @MutationMapping
    public Goal updateGoalProgress(@Argument UpdateGoalProgressInput input) {
        return goalService.updateProgress(input.id(), input.currentAmount());
    }

    @MutationMapping
    public Boolean deleteGoal(@Argument Long id) {
        goalService.deleteGoal(id);
        return Boolean.TRUE;
    }

    @SchemaMapping(typeName = "Account", field = "transactions")
    public List<Transaction> transactions(Account account,
                                          @Argument Optional<Integer> limit,
                                          @Argument(name = "from") Optional<OffsetDateTime> from,
                                          @Argument(name = "to") Optional<OffsetDateTime> to) {
        Integer resolvedLimit = limit.filter(l -> l > 0).orElse(null);
        return transactionService.findTransactions(
                account.getId(),
                resolvedLimit,
                from.orElse(null),
                to.orElse(null)
        );
    }
}

