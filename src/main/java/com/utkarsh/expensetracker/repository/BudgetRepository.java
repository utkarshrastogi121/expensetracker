package com.utkarsh.expensetracker.repository;

import com.utkarsh.expensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserIdAndMonthYear(Long userId, String monthYear);
}