package com.utkarsh.expensetracker.dto;

import com.utkarsh.expensetracker.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDTO {
    private ExpenseDTO expense;
    private boolean budgetExceeded;
    private String alertMessage;
}