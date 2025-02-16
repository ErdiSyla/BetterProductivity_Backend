package com.erdi.Exceptions;

public class UserWithSameEmailException extends RuntimeException {
	public UserWithSameEmailException(String message) {
		super(message);
	}
}
