package com.utkarsh.expensetracker.repository;

import com.utkarsh.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUserId(Long userId, Pageable pageable);
    Page<Expense> findByCategoryId(Long categoryId, Pageable pageable);

    // High-performance query to get total spent in a date range
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    Double getTotalSpentByUserIdInPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}