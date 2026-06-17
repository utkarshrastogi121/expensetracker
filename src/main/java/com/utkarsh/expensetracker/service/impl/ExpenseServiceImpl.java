package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.dto.BudgetResponseDTO;
import com.utkarsh.expensetracker.dto.ExpenseDTO;
import com.utkarsh.expensetracker.entity.*;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.UnauthorizedAccessException;
import com.utkarsh.expensetracker.repository.*;
import com.utkarsh.expensetracker.service.ExpenseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
            @CacheEvict(value = "all_expenses", allEntries = true),
            @CacheEvict(value = "filtered_expenses", allEntries = true),
            @CacheEvict(value = "search_expenses", allEntries = true),
            @CacheEvict(value = "analytics_summary", allEntries = true),
            @CacheEvict(value = "analytics_category", allEntries = true),
            @CacheEvict(value = "analytics_monthly", allEntries = true)
    })
    public BudgetResponseDTO createExpense(Expense expense, String email, Long categoryId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        expense.setUser(user);
        expense.setCategory(category);
        if (expense.getDate() == null) expense.setDate(LocalDate.now());

        Expense savedExpense = expenseRepository.save(expense);
        return checkBudgetAndBuildResponse(savedExpense, user.getId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_expenses", allEntries = true),
            @CacheEvict(value = "category_expenses", allEntries = true),
            @CacheEvict(value = "all_expenses", allEntries = true),
            @CacheEvict(value = "filtered_expenses", allEntries = true),
            @CacheEvict(value = "search_expenses", allEntries = true),
            @CacheEvict(value = "analytics_summary", allEntries = true),
            @CacheEvict(value = "analytics_category", allEntries = true),
            @CacheEvict(value = "analytics_monthly", allEntries = true)
    })
    public BudgetResponseDTO updateExpense(Long id, Expense expenseDetails, String email, Long categoryId) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!existingExpense.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Unauthorized actions on this resource.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        existingExpense.setTitle(expenseDetails.getTitle());
        existingExpense.setAmount(expenseDetails.getAmount());
        existingExpense.setCategory(category);
        if (expenseDetails.getDate() != null) existingExpense.setDate(expenseDetails.getDate());

        Expense updatedExpense = expenseRepository.save(existingExpense);
        return checkBudgetAndBuildResponse(updatedExpense, existingExpense.getUser().getId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_expenses", allEntries = true),
            @CacheEvict(value = "category_expenses", allEntries = true),
            @CacheEvict(value = "all_expenses", allEntries = true),
            @CacheEvict(value = "filtered_expenses", allEntries = true),
            @CacheEvict(value = "search_expenses", allEntries = true),
            @CacheEvict(value = "analytics_summary", allEntries = true),
            @CacheEvict(value = "analytics_category", allEntries = true),
            @CacheEvict(value = "analytics_monthly", allEntries = true)
    })
    public void deleteExpense(Long id, String email) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!existingExpense.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Unauthorized actions on this resource.");
        }
        expenseRepository.delete(existingExpense);
    }

    // FIX: Removed @Cacheable to avoid complex PageImpl serialization errors over Redis
    @Override
    public Page<ExpenseDTO> getExpensesByUser(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return expenseRepository.findByUserId(user.getId(), pageable).map(this::mapToDTO);
    }

    // FIX: Removed @Cacheable
    @Override
    public Page<ExpenseDTO> filterExpensesByDate(String email, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return expenseRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate, pageable).map(this::mapToDTO);
    }

    // FIX: Removed @Cacheable
    @Override
    public Page<ExpenseDTO> searchExpenses(String email, String keyword, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return expenseRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), keyword, pageable).map(this::mapToDTO);
    }

    // FIX: Removed @Cacheable
    @Override
    public Page<ExpenseDTO> getExpensesByCategory(Long categoryId, Pageable pageable) {
        return expenseRepository.findByCategoryId(categoryId, pageable).map(this::mapToDTO);
    }

    // FIX: Removed @Cacheable
    @Override
    public Page<ExpenseDTO> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable).map(this::mapToDTO);
    }

    private BudgetResponseDTO checkBudgetAndBuildResponse(Expense expense, Long userId) {
        int expenseMonth = expense.getDate().getMonthValue();
        int expenseYear = expense.getDate().getYear();
        Long categoryId = expense.getCategory().getId();
        String categoryName = expense.getCategory().getName();

        Double totalCategorySpent = expenseRepository.getTotalSpentByUserAndCategoryInMonth(
                userId, categoryId, expenseMonth, expenseYear
        );

        var budgetOpt = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, categoryId, expenseMonth, expenseYear
        );

        boolean exceeded = false;
        String message;

        if (budgetOpt.isPresent()) {
            Double limit = budgetOpt.get().getAmount();
            if (totalCategorySpent > limit) {
                exceeded = true;
                double overage = totalCategorySpent - limit;
                message = categoryName + " budget exceeded by ₹" + String.format("%.0f", overage);
            } else {
                message = "Within budget. Remaining for " + categoryName + ": ₹" + String.format("%.0f", (limit - totalCategorySpent));
            }
        } else {
            message = "No budget set for category " + categoryName;
        }

        return new BudgetResponseDTO(mapToDTO(expense), exceeded, message);
    }

    private ExpenseDTO mapToDTO(Expense expense) {
        return new ExpenseDTO(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getDate(),
                expense.getUser() != null ? expense.getUser().getId() : null,
                expense.getCategory() != null ? expense.getCategory().getId() : null,
                expense.getCategory() != null ? expense.getCategory().getName() : null
        );
    }
}