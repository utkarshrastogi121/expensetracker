package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.entity.*;
import com.utkarsh.expensetracker.repository.*;
import com.utkarsh.expensetracker.service.ExpenseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user_expenses", allEntries = true),
            @CacheEvict(value = "category_expenses", allEntries = true),
            @CacheEvict(value = "all_expenses", allEntries = true)
    })
    public BudgetResponseDTO createExpense(Expense expense, Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setUser(user);
        expense.setCategory(category);

        if (expense.getDate() == null) expense.setDate(LocalDate.now());

        // Save the Expense
        Expense savedExpense = expenseRepository.save(expense);

        // Calculate Monthly Range
        LocalDate startOfMonth = expense.getDate().withDayOfMonth(1);
        LocalDate endOfMonth = expense.getDate().withDayOfMonth(expense.getDate().lengthOfMonth());
        String monthKey = expense.getDate().format(DateTimeFormatter.ofPattern("MM-yyyy"));

        //  Get total spent for the user this month from DB
        Double totalSpent = expenseRepository.getTotalSpentByUserIdInPeriod(userId, startOfMonth, endOfMonth);

        // Check against Budget
        var budgetOpt = budgetRepository.findByUserIdAndMonthYear(userId, monthKey);

        boolean exceeded = false;
        String message = "Expense saved successfully.";

        if (budgetOpt.isPresent()) {
            Double limit = budgetOpt.get().getMonthlyLimit();
            if (totalSpent > limit) {
                exceeded = true;
                message = "BUDGET EXCEEDED! Total spent: " + totalSpent + " | Limit: " + limit;
            } else {
                message = "Within budget. Remaining: " + (limit - totalSpent);
            }
        } else {
            message = "No budget set for " + monthKey;
        }

        return new BudgetResponseDTO(savedExpense, exceeded, message);
    }

    @Override
    @Cacheable(value = "user_expenses", key = "#userId + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<Expense> getExpensesByUser(Long userId, Pageable pageable) {
        System.out.println(">>> Fetching paginated expenses from DATABASE for user: " + userId);
        return expenseRepository.findByUserId(userId, pageable);
    }

    @Override
    @Cacheable(value = "category_expenses", key = "#categoryId + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<Expense> getExpensesByCategory(Long categoryId, Pageable pageable) {
        System.out.println(">>> Fetching paginated expenses from DATABASE for category: " + categoryId);
        return expenseRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    @Cacheable(value = "all_expenses", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<Expense> getAllExpenses(Pageable pageable) {
        System.out.println(">>> Fetching global paginated expenses from DATABASE");
        return expenseRepository.findAll(pageable);
    }
}