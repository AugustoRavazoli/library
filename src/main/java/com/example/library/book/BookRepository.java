package com.example.library.book;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.library.user.User;

public interface BookRepository extends JpaRepository<Book, Long> {

  boolean existsByIdAndOwner(long id, User owner);
			
  boolean existsByTitleAndOwner(String title, User owner);

  boolean existsByCategoryId(long id);
	
  Optional<Book> findByIdAndOwner(long id, User owner);
	
  Page<Book> findAllByOwner(User owner, Pageable pageable);

  Page<Book> findAllByTitleContainingIgnoreCaseAndOwner(String title, User owner, Pageable pageable);
	
  void deleteByIdAndOwner(long id, User owner);

}
