package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.dto.AnalyticsSummaryDTO;
import com.utkarsh.expensetracker.dto.CategoryExpenseDTO;
import com.utkarsh.expensetracker.dto.MonthlyExpenseDTO;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.repository.ExpenseRepository;
import com.utkarsh.expensetracker.repository.UserRepository;
import com.utkarsh.expensetracker.service.AnalyticsService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public AnalyticsServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "analytics_summary", key = "#email")
    public AnalyticsSummaryDTO getSummary(String email) {
        System.out.println(">>> Generating ANALYTICS SUMMARY from DATABASE for: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Double totalExpense = expenseRepository.getTotalExpenseByUserId(user.getId());
        Long totalTransactions = expenseRepository.getTotalTransactionsByUserId(user.getId());

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        Double currentMonthExpense = expenseRepository.getTotalSpentByUserIdInPeriod(user.getId(), startOfMonth, endOfMonth);

        return new AnalyticsSummaryDTO(totalExpense, totalTransactions, currentMonthExpense);
    }

    @Override
    @Cacheable(value = "analytics_category", key = "#email")
    public List<CategoryExpenseDTO> getCategoryExpenses(String email) {
        System.out.println(">>> Generating CATEGORY ANALYTICS from DATABASE for: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return expenseRepository.getCategoryWiseExpenses(user.getId());
    }

    @Override
    @Cacheable(value = "analytics_monthly", key = "#email")
    public List<MonthlyExpenseDTO> getMonthlyExpenses(String email) {
        System.out.println(">>> Generating MONTHLY ANALYTICS from DATABASE for: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException("User not found with email: " + email));

        List<Object[]> rawResults = expenseRepository.getMonthlyExpensesRaw(user.getId());

        // Manually map the Object[] elements to your clean DTO models safely
        return rawResults.stream()
                .map(row -> {
                    String monthName = row[0] != null ? row[0].toString() : "Unknown";
                    Double totalAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    return new MonthlyExpenseDTO(monthName, totalAmount);
                })
                .collect(java.util.stream.Collectors.toList());
    }
}