package com.finwise.core.domain.service;

import com.finwise.core.domain.exception.ResourceNotFoundException;
import com.finwise.core.domain.model.Goal;
import com.finwise.core.domain.repository.GoalRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> findByUser(String userId) {
        return goalRepository.findByUserId(userId);
    }

    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada: " + id));
    }

    @Transactional
    public Goal createGoal(String userId, String name, BigDecimal targetAmount, BigDecimal currentAmount,
                           LocalDate targetDate) {
        OffsetDateTime now = OffsetDateTime.now();
        Goal goal = new Goal(userId, name, targetAmount, currentAmount, targetDate, now, now);
        return goalRepository.save(goal);
    }

    @Transactional
    public Goal updateProgress(Long id, BigDecimal currentAmount) {
        Goal goal = findById(id);
        if (currentAmount != null) {
            goal.setCurrentAmount(currentAmount);
        }
        goal.setUpdatedAt(OffsetDateTime.now());
        return goalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Meta no encontrada: " + id);
        }
        goalRepository.deleteById(id);
    }
}




