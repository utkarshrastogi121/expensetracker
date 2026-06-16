package com.utkarsh.expensetracker.controller;

import com.utkarsh.expensetracker.entity.Category;
import com.utkarsh.expensetracker.service.CategoryService;
import jakarta.validation.Valid; // Added Import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category create(@Valid @RequestBody Category category) { // Added @Valid
        return categoryService.createCategory(category);
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }
}