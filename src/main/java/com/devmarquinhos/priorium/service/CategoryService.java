package com.devmarquinhos.priorium.service;

import com.devmarquinhos.priorium.dto.CategoryRequest;
import com.devmarquinhos.priorium.dto.CategoryResponse;
import com.devmarquinhos.priorium.model.Category;
import com.devmarquinhos.priorium.model.Task;
import com.devmarquinhos.priorium.repository.CategoryRepository;
import com.devmarquinhos.priorium.model.User;
import com.devmarquinhos.priorium.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
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

    public CategoryResponse updateCategory(Long id, CategoryRequest request, User loggedUser) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if (!category.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para alterar esta categoria.");
        }

        category.setTitle(request.title());
        category.setColor(request.color());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(updatedCategory.getId(), updatedCategory.getTitle(), updatedCategory.getColor());
    }

    public void deleteCategory(Long id, User loggedUser) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if (!category.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para eliminar esta categoria.");
        }

        List<Task> tasksToUnlink = taskRepository.findByCategoryId(id);

        for (Task task : tasksToUnlink) {
            task.setCategory(null);
            taskRepository.save(task);
        }

        categoryRepository.delete(category);
    }
}
