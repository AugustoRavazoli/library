package com.example.library.category;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.example.library.book.BookRepository;
import com.example.library.user.User;

@Service
@Transactional
public class CategoryService {

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
	
  private final CategoryRepository categoryRepository;
  private final BookRepository bookRepository;
	
  public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
    this.categoryRepository = categoryRepository;
    this.bookRepository = bookRepository;
  }
	
  public void createCategory(Category category, User owner) {
    if (categoryRepository.existsByNameAndOwner(category.getName(), owner)) {
      throw new CategoryAlreadyExistsException("Esta categoria já existe");
    }
    category.setOwner(owner);
    categoryRepository.save(category);
    logger.info("Category {} created for user {}", category.getName(), owner.getUsername());
  }

  public Category findCategory(long id, User owner) {
    return categoryRepository.findByIdAndOwner(id, owner)
      .orElseThrow(CategoryNotFoundException::new);
  }

  public List<Category> listCategories(User owner) {
    return categoryRepository.findAllByOwner(owner);
  }

  public void editCategory(long id, Category newCategory, User owner) {
    categoryRepository.findByIdAndOwner(id, owner)
      .map(category -> {
        if (categoryRepository.existsByNameAndOwner(newCategory.getName(), owner)
          && !category.getName().equals(newCategory.getName())
        ) {
          throw new CategoryAlreadyExistsException("Esta categoria já existe");
        }
        category.setName(newCategory.getName());
        return categoryRepository.save(category);
      })
      .orElseThrow(CategoryNotFoundException::new);
    logger.info("Category {} of user {} was edited", newCategory.getName(), owner.getUsername());
  }

  public void deleteCategory(long id, User owner) {
    if (!categoryRepository.existsByIdAndOwner(id, owner)) {
      throw new CategoryNotFoundException();
    }
    if (bookRepository.existsByCategoryId(id)) {
      throw new CategoryInUseException("Categoria associada a um livro não pode ser deletada");
    }
    categoryRepository.deleteByIdAndOwner(id, owner);
    logger.info("Deleted category widh id {} of user {}", id, owner.getUsername());
  }

}
