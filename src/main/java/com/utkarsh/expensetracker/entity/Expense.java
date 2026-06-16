package com.utkarsh.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    @JsonIgnore
    private Category category;
}