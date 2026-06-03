package com.furnituree.furnituree.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furnituree.furnituree.dto.CategoryRequest;
import com.furnituree.furnituree.exception.BadRequestException;
import com.furnituree.furnituree.exception.ResourceNotFoundException;
import com.furnituree.furnituree.model.Category;
import com.furnituree.furnituree.repo.category_repo;
import com.furnituree.furnituree.repo.product_repo;

@Service
public class CategoryService {

    private final category_repo categoryRepo;
    private final product_repo productRepo;

    public CategoryService(category_repo categoryRepo, product_repo productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
    }

    public List<Category> getAllCategories(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return categoryRepo.findByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim());
        }
        return categoryRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Category getOneCategory(Long id) {
        return findCategory(id);
    }

    @Transactional
    public Category createCategory(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepo.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Category name already exists");
        }

        Category category = new Category();
        applyCategoryRequest(category, request);
        return categoryRepo.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = request.getName().trim();

        categoryRepo.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Category name already exists");
            }
        });

        applyCategoryRequest(category, request);
        return categoryRepo.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        long productCount = productRepo.countByCategoryId(id);
        if (productCount > 0) {
            throw new BadRequestException("Cannot delete category because it still has " + productCount + " products");
        }

        categoryRepo.delete(category);
    }

    private Category findCategory(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    private void applyCategoryRequest(Category category, CategoryRequest request) {
        category.setName(request.getName().trim());
        category.setDescription(blankToNull(request.getDescription()));
        category.setImageUrl(blankToNull(request.getImageUrl()));
        category.setStatus(request.getStatus() == null || request.getStatus().isBlank()
                ? "ACTIVE"
                : request.getStatus().trim().toUpperCase());
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
