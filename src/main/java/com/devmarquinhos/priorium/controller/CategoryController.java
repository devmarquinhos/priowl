package com.devmarquinhos.priorium.controller;

import com.devmarquinhos.priorium.dto.CategoryRequest;
import com.devmarquinhos.priorium.dto.CategoryResponse;
import com.devmarquinhos.priorium.model.User;
import com.devmarquinhos.priorium.service.CategoryService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoryRequest request, @AuthenticationPrincipal User loggedUser) {
        try {
            CategoryResponse response = categoryService.updateCategory(id, request, loggedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest req, @AuthenticationPrincipal User loggedUser) {
        CategoryResponse res = categoryService.createCategory(req, loggedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listAll(@AuthenticationPrincipal User loggedUser) {
        List<CategoryResponse> categories = categoryService.getCategoriesByUser(loggedUser);

        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User loggedUser) {
        try {
            categoryService.deleteCategory(id, loggedUser);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
