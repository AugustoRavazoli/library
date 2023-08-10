package com.example.library.book;

import org.springframework.stereotype.Component;
import com.example.library.category.Category;

@Component
public class BookMapper {

  public Book toEntity(BookRequest bookRequest) {
    return new Book(
      bookRequest.getTitle(),
      bookRequest.getDescription(),
      new Category(bookRequest.getCategory())
    );
  }

  public BookResponse toResponse(Book book) {
    return new BookResponse(
      book.getId(),
      book.getTitle(),
      book.getDescription(),
      book.getCategory().getName()
    );
  }

}
