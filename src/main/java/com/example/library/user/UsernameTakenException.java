package com.example.library.user;

public class UsernameTakenException extends RuntimeException {

	private static final long serialVersionUID = 8005439870555292754L;

	public UsernameTakenException(String message) {
		super(message);
	}
	
}
