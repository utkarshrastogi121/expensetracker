package com.utkarsh.expensetracker.service;

import com.utkarsh.expensetracker.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
}