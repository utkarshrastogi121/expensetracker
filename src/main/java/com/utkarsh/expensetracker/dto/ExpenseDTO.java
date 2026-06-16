package com.utkarsh.expensetracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseDTO {

    private Long id;
    private String title;
    private Double amount;
    private LocalDate date;

    private Long userId;
    private Long categoryId;
}