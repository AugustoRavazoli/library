package com.example.library.category;

public class CategoryInUseException extends RuntimeException {

	private static final long serialVersionUID = -3254206303209472001L;

  public CategoryInUseException(String message) {
    super(message);
  }

}
