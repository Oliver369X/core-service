package com.finwise.core.domain.repository;

import com.finwise.core.domain.model.Budget;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndPeriodEndGreaterThanEqual(String userId, LocalDate date);

    List<Budget> findByUserId(String userId);
}
