package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.BudgetDTO;
import com.utkarsh.expensetracker.entity.Budget;
import com.utkarsh.expensetracker.service.BudgetService;
import jakarta.validation.Valid; // Added Import
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(
            @Valid @RequestBody Budget budget, // Added @Valid
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        BudgetDTO dto = budgetService.createOrUpdateBudget(budget, userDetails.getUsername(), categoryId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<BudgetDTO>> getBudgets(@AuthenticationPrincipal UserDetails userDetails) {
        List<BudgetDTO> budgets = budgetService.getCurrentMonthBudgets(userDetails.getUsername());
        return ResponseEntity.ok(budgets);
    }
}