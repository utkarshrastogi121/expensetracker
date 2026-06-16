package com.utkarsh.expensetracker.service.impl;

import com.utkarsh.expensetracker.entity.Category;
import com.utkarsh.expensetracker.repository.CategoryRepository;
import com.utkarsh.expensetracker.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @CachePut(value = "category", key = "#result.id")
    @CacheEvict(value = "all_categories", allEntries = true)
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    @Cacheable(value = "all_categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}