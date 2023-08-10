package com.example.library.category;

public class CategoryAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -7158831271416376159L;
	
	public CategoryAlreadyExistsException(String message) {
		super(message);
	}

}
