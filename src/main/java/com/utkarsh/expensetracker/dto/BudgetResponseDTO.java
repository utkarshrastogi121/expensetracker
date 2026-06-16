package com.utkarsh.expensetracker.dto;

import com.utkarsh.expensetracker.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetResponseDTO {
    private Expense expense;
    private boolean isBudgetExceeded;
    private String alertMessage;
}