package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.entity.Expense;
import com.utkarsh.expensetracker.service.ExpenseService;
import org.springframework.data.domain.Page;    // Add
import org.springframework.data.domain.Pageable; // Add
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public BudgetResponseDTO create(
            @RequestBody Expense expense,
            @RequestParam Long userId,
            @RequestParam Long categoryId
    ) {
        return expenseService.createExpense(expense, userId, categoryId);
    }

    @GetMapping("/user/{userId}")
    public Page<Expense> getByUser(@PathVariable Long userId, Pageable pageable) {
        return expenseService.getExpensesByUser(userId, pageable);
    }

    @GetMapping("/category/{categoryId}")
    public Page<Expense> getByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return expenseService.getExpensesByCategory(categoryId, pageable);
    }

    @GetMapping
    public Page<Expense> getAll(Pageable pageable) {
        return expenseService.getAllExpenses(pageable);
    }
}