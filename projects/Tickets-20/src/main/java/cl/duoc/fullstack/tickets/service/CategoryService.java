package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.CategoryRequest;
import cl.duoc.fullstack.tickets.model.Category;
import cl.duoc.fullstack.tickets.respository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category create(CategoryRequest request) {
    if (categoryRepository.existsByNameIgnoreCase(request.name())) {
      throw new IllegalArgumentException("Category with this name already exists");
    }
    Category category = new Category();
    category.setName(request.name());
    category.setDescription(request.description());
    return categoryRepository.save(category);
  }

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Optional<Category> findById(Long id) {
    return categoryRepository.findById(id);
  }

  public Optional<Category> update(Long id, CategoryRequest request) {
    return categoryRepository.findById(id).map(category -> {
      if (!category.getName().equalsIgnoreCase(request.name())
          && categoryRepository.existsByNameIgnoreCase(request.name())) {
        throw new IllegalArgumentException("Category with this name already exists");
      }
      category.setName(request.name());
      category.setDescription(request.description());
      return categoryRepository.save(category);
    });
  }

  public boolean deleteById(Long id) {
    if (categoryRepository.existsById(id)) {
      categoryRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
