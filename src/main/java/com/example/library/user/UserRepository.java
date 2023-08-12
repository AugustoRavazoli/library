package com.example.library.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String name);
	
  Optional<User> findByUsername(String name);
	
}
