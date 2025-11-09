package com.finwise.core.domain.service;

import com.finwise.core.domain.exception.ResourceNotFoundException;
import com.finwise.core.domain.model.Account;
import com.finwise.core.domain.model.Transaction;
import com.finwise.core.domain.model.TransactionType;
import com.finwise.core.domain.repository.AccountRepository;
import com.finwise.core.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> findByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByOccurredAtDesc(accountId);
    }

    public List<Transaction> findTransactions(Long accountId, Integer limit, OffsetDateTime from, OffsetDateTime to) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByOccurredAtDesc(accountId);
        return transactions.stream()
                .filter(tx -> from == null || !tx.getOccurredAt().isBefore(from))
                .filter(tx -> to == null || !tx.getOccurredAt().isAfter(to))
                .limit(limit != null && limit > 0 ? limit : transactions.size())
                .toList();
    }

    @Transactional
    public Transaction registerTransaction(Long accountId, TransactionType type, BigDecimal amount, String description,
                                           OffsetDateTime occurredAt) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + accountId));

        OffsetDateTime timestamp = OffsetDateTime.now();
        Transaction transaction = new Transaction(account, type, amount, description,
                occurredAt != null ? occurredAt : timestamp, timestamp, timestamp);
        Transaction saved = transactionRepository.save(transaction);

        BigDecimal newBalance = calculateNewBalance(account.getBalance(), type, amount);
        account.setBalance(newBalance);
        account.setUpdatedAt(timestamp);
        accountRepository.save(account);

        return saved;
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, TransactionType type, BigDecimal amount) {
        if (TransactionType.EXPENSE.equals(type)) {
            return currentBalance.subtract(amount);
        }
        return currentBalance.add(amount);
    }
}
