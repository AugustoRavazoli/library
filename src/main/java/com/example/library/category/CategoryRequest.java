package com.example.library.category;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

  @NotBlank(message = "Nome não pode estar em branco")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
