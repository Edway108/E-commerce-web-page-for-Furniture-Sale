package com.furnituree.furnituree.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furnituree.furnituree.dto.CategoryRequest;
import com.furnituree.furnituree.model.Category;
import com.furnituree.furnituree.service.CategoryService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/categories")
public class category_controller {

    private final CategoryService categoryService;

    public category_controller(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/findall")
    public List<Category> getAllCategories(@RequestParam(required = false) String keyword) {
        return categoryService.getAllCategories(keyword);
    }

    @GetMapping("/{id}")
    public Category getOneCategory(@PathVariable Long id) {
        return categoryService.getOneCategory(id);
    }

    @PostMapping("/addcategory")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/update/{id}")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
