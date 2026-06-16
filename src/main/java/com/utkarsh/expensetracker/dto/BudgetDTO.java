package com.utkarsh.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Double amount;
    private Integer month;
    private Integer year;
    private Long categoryId;
    private String categoryName;
}