package com.finwise.core.domain.repository;

import com.finwise.core.domain.model.Transaction;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByOccurredAtDesc(Long accountId);

    List<Transaction> findByAccountUserIdAndOccurredAtBetweenOrderByOccurredAtDesc(
            String userId, OffsetDateTime startDate, OffsetDateTime endDate);
}




