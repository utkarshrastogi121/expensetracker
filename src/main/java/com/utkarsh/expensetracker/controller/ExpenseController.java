package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.dto.ExpenseDTO;
import com.utkarsh.expensetracker.entity.Expense;
import com.utkarsh.expensetracker.service.ExpenseService;
import jakarta.validation.Valid; // Added Import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createExpense(
            @Valid @RequestBody Expense expense, // Added @Valid
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(expenseService.createExpense(expense, userDetails.getUsername(), categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody Expense expense, // Added @Valid
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expense, userDetails.getUsername(), categoryId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        expenseService.deleteExpense(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(expenseService.getExpensesByUser(userDetails.getUsername(), pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ExpenseDTO>> filterExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(expenseService.filterExpensesByDate(userDetails.getUsername(), startDate, endDate, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseDTO>> searchExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(expenseService.searchExpenses(userDetails.getUsername(), q, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(expenseService.getExpensesByCategory(categoryId, pageable));
    }
}