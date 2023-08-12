package com.example.library.book;

public class TitleTakenException extends RuntimeException {

  private static final long serialVersionUID = -3561736485246957643L;

  public TitleTakenException(String message) {
    super(message);
  }

}
