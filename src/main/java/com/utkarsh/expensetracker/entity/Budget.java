package com.utkarsh.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "budgets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category_id", "budget_month", "budget_year"})
})
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Budget amount cannot be null")
    @Positive(message = "Budget amount must be a positive number greater than 0")
    // FIX: Match the actual PostgreSQL column named "monthly_limit"
    @Column(name = "monthly_limit", nullable = false)
    private Double amount;

    @NotNull(message = "Month cannot be null")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(name = "budget_month", nullable = false)
    private Integer month; // e.g., 6 for June

    @NotNull(message = "Year cannot be null")
    @Min(value = 2000, message = "Year must be a valid 4-digit number")
    @Column(name = "budget_year", nullable = false)
    private Integer year;  // e.g., 2026

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Maps the underlying constraint field name that PostgreSQL expects
    @Column(name = "month_year", nullable = true)
    private String monthYear;

    // Automatically populates the constraint block field before Hibernate runs its insert statement
    @PrePersist
    @PreUpdate
    public void assignMonthYearConstraintValue() {
        if (this.month != null && this.year != null) {
            this.monthYear = String.format("%02d-%d", this.month, this.year);
        }
    }
}