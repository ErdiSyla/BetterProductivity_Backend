package com.erdi.Exceptions;

public class NoUserWithEmailException extends RuntimeException {
	public NoUserWithEmailException(String message) {
		super(message);
	}
}
