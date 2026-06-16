package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.dto.BudgetDTO;
import com.utkarsh.expensetracker.entity.Budget;
import com.utkarsh.expensetracker.entity.Category;
import com.utkarsh.expensetracker.entity.User;
import com.utkarsh.expensetracker.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.utkarsh.expensetracker.repository.BudgetRepository;
import com.utkarsh.expensetracker.repository.CategoryRepository;
import com.utkarsh.expensetracker.repository.UserRepository;
import com.utkarsh.expensetracker.service.BudgetService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetServiceImpl(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user_budgets", allEntries = true)
    public BudgetDTO createOrUpdateBudget(Budget budget, String email, Long categoryId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (budget.getMonth() == null) budget.setMonth(LocalDate.now().getMonthValue());
        if (budget.getYear() == null) budget.setYear(LocalDate.now().getYear());

        Budget targetBudget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                        user.getId(), categoryId, budget.getMonth(), budget.getYear())
                .orElse(budget);

        targetBudget.setAmount(budget.getAmount());
        targetBudget.setUser(user);
        targetBudget.setCategory(category);

        Budget savedBudget = budgetRepository.save(targetBudget);
        return mapToDTO(savedBudget);
    }

    @Override
    @Cacheable(value = "user_budgets", key = "#email")
    public List<BudgetDTO> getCurrentMonthBudgets(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        LocalDate today = LocalDate.now();
        return budgetRepository.findByUserIdAndMonthAndYear(user.getId(), today.getMonthValue(), today.getYear())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BudgetDTO mapToDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(budget.getId());
        dto.setAmount(budget.getAmount());
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());
        dto.setCategoryId(budget.getCategory().getId());
        dto.setCategoryName(budget.getCategory().getName());
        return dto;
    }
}