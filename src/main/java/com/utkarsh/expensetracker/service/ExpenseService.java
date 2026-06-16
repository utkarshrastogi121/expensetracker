package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;    // Add
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ExpenseService {

//    Expense createExpense(Expense expense, Long userId, Long categoryId);

    BudgetResponseDTO createExpense(Expense expense, Long userId, Long categoryId);

    Page<Expense> getExpensesByUser(Long userId, Pageable pageable);

    Page<Expense> getExpensesByCategory(Long categoryId, Pageable pageable);

    Page<Expense> getAllExpenses(Pageable pageable);}