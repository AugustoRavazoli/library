package com.example.library.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toEntity(UserRequest userRequest) {
    return new User(
      userRequest.getUsername(),
      userRequest.getPassword()
    );
  }

}
