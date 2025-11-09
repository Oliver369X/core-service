package com.finwise.core.domain.service;

import com.finwise.core.domain.exception.ResourceNotFoundException;
import com.finwise.core.domain.model.Account;
import com.finwise.core.domain.model.AccountType;
import com.finwise.core.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> findByUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + id));
    }

    @Transactional
    public Account createAccount(String userId, String name, AccountType type, BigDecimal balance, String currency) {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(userId, name, type, balance, currency, now, now);
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(Long id, String name, AccountType type, String currency) {
        Account account = findById(id);
        if (name != null) {
            account.setName(name);
        }
        if (type != null) {
            account.setType(type);
        }
        if (currency != null) {
            account.setCurrency(currency);
        }
        account.setUpdatedAt(OffsetDateTime.now());
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cuenta no encontrada: " + id);
        }
        accountRepository.deleteById(id);
    }

    @Transactional
    public Account updateBalance(Long id, BigDecimal newBalance) {
        Account account = findById(id);
        account.setBalance(newBalance);
        account.setUpdatedAt(OffsetDateTime.now());
        return accountRepository.save(account);
    }
}




