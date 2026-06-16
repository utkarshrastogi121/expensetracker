package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.dto.AnalyticsSummaryDTO;
import com.utkarsh.expensetracker.dto.CategoryExpenseDTO;
import com.utkarsh.expensetracker.dto.MonthlyExpenseDTO;
import com.utkarsh.expensetracker.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analyticsService.getSummary(userDetails.getUsername()));
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryExpenseDTO>> getCategoryExpenses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analyticsService.getCategoryExpenses(userDetails.getUsername()));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyExpenseDTO>> getMonthlyExpenses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analyticsService.getMonthlyExpenses(userDetails.getUsername()));
    }
}