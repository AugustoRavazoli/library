package com.example.library.category;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  public Category toEntity(CategoryRequest categoryRequest) {
    return new Category(
      categoryRequest.getName()
    );
  }

  public CategoryResponse toResponse(Category category) {
    return new CategoryResponse(
      category.getId(),
      category.getName()
    );
  }

}
