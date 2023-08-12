package com.example.library.book;

import jakarta.validation.constraints.NotBlank;

public class BookRequest {
	
  @NotBlank(message = "T�tulo n�o pode estar em branco")
  private String title;
	
  @NotBlank(message = "Descri��o n�o pode estar em branco")
  private String description;
	
  @NotBlank(message = "Categoria n�o pode estar em branco")
  private String category;

  public String getTitle() {
    return title;
  }
	
  public void setTitle(String title) {
    this.title = title;
  }
	
  public String getDescription() {
    return description;
  }
	
  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }
	
  public void setCategory(String category) {
    this.category = category;
  }

}
