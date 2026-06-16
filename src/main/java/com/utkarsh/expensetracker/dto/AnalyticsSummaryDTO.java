package com.utkarsh.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double totalExpense;
    private Long totalTransactions;
    private Double currentMonthExpense;
}