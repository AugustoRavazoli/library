package com.example.library.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.category.CategoryRepository;
import com.example.library.user.User;

@Service
@Transactional
public class BookService {

  private static final Logger logger = LoggerFactory.getLogger(BookService.class);
	
  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;
	
  public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
    this.bookRepository = bookRepository;
    this.categoryRepository = categoryRepository;
  }
	
  public void createBook(Book book, User owner) {
    if (bookRepository.existsByTitleAndOwner(book.getTitle(), owner)) {
      throw new TitleTakenException("Título de livro já em uso");
    }
    var category = categoryRepository.findByNameAndOwner(book.getCategory().getName(), owner)
      .orElseThrow(CategoryMismatchException::new);
    book.setCategory(category);
    book.setOwner(owner);
    bookRepository.save(book);
    logger.info("Book {} created for user {}", book.getTitle(), owner.getUsername());
  }
	
  public Book findBook(long id, User owner) {
    return bookRepository.findByIdAndOwner(id, owner)
      .orElseThrow(BookNotFoundException::new);
  }
	
  public Page<Book> listBooks(String title, User owner, int page) {
    var size = 6;
    var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    return bookRepository.findAllByTitleContainingIgnoreCaseAndOwner(title, owner, pageable);
  }

  public void editBook(long id, Book newBook, User owner) {
    bookRepository.findByIdAndOwner(id, owner)
      .map(book -> {
        if (bookRepository.existsByTitleAndOwner(newBook.getTitle(), owner) 
          && !book.getTitle().equals(newBook.getTitle())
        ) {
          throw new TitleTakenException("Título de livro já em uso");
        }
        var category = categoryRepository.findByNameAndOwner(newBook.getCategory().getName(), owner)
          .orElseThrow(CategoryMismatchException::new);
        book.setTitle(newBook.getTitle());
        book.setDescription(newBook.getDescription());
        book.setCategory(category);
        return bookRepository.save(book);
      })
      .orElseThrow(BookNotFoundException::new);
    logger.info("Book {} of user {} was edited", newBook.getTitle(), owner.getUsername());
  }
	
  public void deleteBook(long id, User owner) {
    if (!bookRepository.existsByIdAndOwner(id, owner)) {
      throw new BookNotFoundException();
    }
    bookRepository.deleteByIdAndOwner(id, owner);
    logger.info("Deleted book widh id {} of user {}", id, owner.getUsername());
  }

}
