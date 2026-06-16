package com.utkarsh.expensetracker.repository;

import com.utkarsh.expensetracker.dto.CategoryExpenseDTO;
import com.utkarsh.expensetracker.dto.MonthlyExpenseDTO;
import com.utkarsh.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUserId(Long userId, Pageable pageable);
    Page<Expense> findByCategoryId(Long categoryId, Pageable pageable);

    // Date filter
    Page<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Keyword Search Method
    Page<Expense> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    Double getTotalSpentByUserIdInPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.category.id = :categoryId " +
            "AND FUNCTION('MONTH', e.date) = :month " +
            "AND FUNCTION('YEAR', e.date) = :year")
    Double getTotalSpentByUserAndCategoryInMonth(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.user.id = :userId")
    Double getTotalExpenseByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
    Long getTotalTransactionsByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.utkarsh.expensetracker.dto.CategoryExpenseDTO(e.category.name, SUM(e.amount)) " +
            "FROM Expense e WHERE e.user.id = :userId GROUP BY e.category.name")
    List<CategoryExpenseDTO> getCategoryWiseExpenses(@Param("userId") Long userId);

    @Query("SELECT FUNCTION('FORMATDATETIME', e.date, 'MMM'), SUM(e.amount) " +
            "FROM Expense e WHERE e.user.id = :userId " +
            "GROUP BY FUNCTION('FORMATDATETIME', e.date, 'MMM')")
    List<Object[]> getMonthlyExpensesRaw(@Param("userId") Long userId);
}