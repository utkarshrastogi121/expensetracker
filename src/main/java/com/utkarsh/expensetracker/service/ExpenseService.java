package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.dto.ExpenseDTO;
import com.utkarsh.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExpenseService {
    BudgetResponseDTO createExpense(Expense expense, String email, Long categoryId);
    BudgetResponseDTO updateExpense(Long id, Expense expenseDetails, String email, Long categoryId);
    void deleteExpense(Long id, String email);

    Page<ExpenseDTO> getExpensesByUser(String email, Pageable pageable);
    Page<ExpenseDTO> filterExpensesByDate(String email, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<ExpenseDTO> searchExpenses(String email, String keyword, Pageable pageable);
    Page<ExpenseDTO> getExpensesByCategory(Long categoryId, Pageable pageable);
    Page<ExpenseDTO> getAllExpenses(Pageable pageable);
}