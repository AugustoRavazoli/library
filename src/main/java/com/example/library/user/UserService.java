package com.example.library.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
	
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }
	
  public void registerUser(User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new UsernameTakenException("J� existe um usu�rio com este nome");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    logger.info("Account created for user {}", user.getUsername());
  }

}
