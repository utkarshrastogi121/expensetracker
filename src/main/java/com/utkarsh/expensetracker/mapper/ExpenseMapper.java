package com.utkarsh.expensetracker.mapper;

import com.utkarsh.expensetracker.dto.ExpenseDTO;
import com.utkarsh.expensetracker.entity.Expense;

public class ExpenseMapper {

    public static ExpenseDTO toDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setUserId(expense.getUser().getId());
        dto.setCategoryId(expense.getCategory().getId());
        return dto;
    }
}