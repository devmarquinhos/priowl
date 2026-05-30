package com.devmarquinhos.priorium.service;

import com.devmarquinhos.priorium.dto.CategoryRequest;
import com.devmarquinhos.priorium.dto.CategoryResponse;
import com.devmarquinhos.priorium.model.Category;
import com.devmarquinhos.priorium.repository.CategoryRepository;
import com.devmarquinhos.priorium.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest req, User loggedUser){
        Category category = new Category();

        category.setTitle(req.title());
        category.setColor(req.color());
        category.setUser(loggedUser);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory.getId(), savedCategory.getTitle(), savedCategory.getColor());
    }

    public List<CategoryResponse> getCategoriesByUser(User loggedUser){
        List<Category> categories = categoryRepository.findByUserId(loggedUser.getId());

        return categories.stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getTitle(), cat.getColor()))
                .collect(Collectors.toList());
    }
}
