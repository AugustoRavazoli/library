package com.example.library.book;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -3254206303209472001L;

}
