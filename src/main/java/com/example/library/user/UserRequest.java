package com.example.library.user;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {

	@NotBlank(message = "Nome não pode estar em branco")
	private String username;
	
	@NotBlank(message = "Senha não pode estar em branco")
	private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
