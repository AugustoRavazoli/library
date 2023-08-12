package com.example.library.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.library.user.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByIdAndOwner(long id, User owner);
	
  boolean existsByNameAndOwner(String name, User owner);

  Optional<Category> findByNameAndOwner(String name, User owner);

  Optional<Category> findByIdAndOwner(long id, User owner);
		
  List<Category> findAllByOwner(User onwer);

  void deleteByIdAndOwner(long id, User owner); 
	
}
