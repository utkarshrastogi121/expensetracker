package com.utkarsh.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private Double amount;
    private LocalDate date;
    private Long userId;
    private Long categoryId;
    private String categoryName;
}