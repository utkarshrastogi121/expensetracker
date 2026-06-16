package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.AnalyticsSummaryDTO;
import com.utkarsh.expensetracker.dto.CategoryExpenseDTO;
import com.utkarsh.expensetracker.dto.MonthlyExpenseDTO;

import java.util.List;

public interface AnalyticsService {
    AnalyticsSummaryDTO getSummary(String email);
    List<CategoryExpenseDTO> getCategoryExpenses(String email);
    List<MonthlyExpenseDTO> getMonthlyExpenses(String email);
}