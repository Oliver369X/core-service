package com.finwise.core.domain.service;

import com.finwise.core.domain.exception.ResourceNotFoundException;
import com.finwise.core.domain.model.Budget;
import com.finwise.core.domain.repository.BudgetRepository;
import com.finwise.core.integration.NotificationGateway;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final NotificationGateway notificationGateway;

    public BudgetService(BudgetRepository budgetRepository, NotificationGateway notificationGateway) {
        this.budgetRepository = budgetRepository;
        this.notificationGateway = notificationGateway;
    }

    public List<Budget> findActiveBudgets(String userId, LocalDate referenceDate) {
        return budgetRepository.findByUserIdAndPeriodEndGreaterThanEqual(userId, referenceDate);
    }

    public List<Budget> findAllBudgets(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado: " + id));
    }

    @Transactional
    public Budget createBudget(String userId, String category, BigDecimal limitAmount, LocalDate start, LocalDate end) {
        OffsetDateTime now = OffsetDateTime.now();
        Budget budget = new Budget(userId, category, limitAmount, start, end, now, now);
        Budget saved = budgetRepository.save(budget);
        notificationGateway.notify(
                "Nuevo presupuesto creado",
                "Se configuró el presupuesto de " + category + ".",
                userId);
        return saved;
    }

    @Transactional
    public Budget updateBudget(Long id, BigDecimal limitAmount, LocalDate start, LocalDate end) {
        Budget budget = findById(id);
        boolean notify = false;

        if (limitAmount != null) {
            budget.setLimitAmount(limitAmount);
            notify = true;
        }
        if (start != null) {
            budget.setPeriodStart(start);
        }
        if (end != null) {
            budget.setPeriodEnd(end);
        }
        budget.setUpdatedAt(OffsetDateTime.now());
        Budget updated = budgetRepository.save(budget);
        if (notify) {
            notificationGateway.notify(
                    "Presupuesto actualizado",
                    "Se actualizó el presupuesto de " + budget.getCategory() + ".",
                    budget.getUserId());
        }
        return updated;
    }

    @Transactional
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Presupuesto no encontrado: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
