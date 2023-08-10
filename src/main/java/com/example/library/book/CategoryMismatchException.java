package com.example.library.book;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class CategoryMismatchException extends RuntimeException {

	private static final long serialVersionUID = 968696003337997975L;

}
