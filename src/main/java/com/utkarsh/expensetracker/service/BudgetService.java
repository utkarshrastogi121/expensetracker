package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.dto.BudgetDTO;
import com.utkarsh.expensetracker.entity.Budget;
import java.util.List;

public interface BudgetService {

    BudgetDTO createOrUpdateBudget(Budget budget, String email, Long categoryId);

    List<BudgetDTO> getCurrentMonthBudgets(String email);
}