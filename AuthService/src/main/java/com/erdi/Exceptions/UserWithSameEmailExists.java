package com.erdi.Exceptions;

public class UserWithSameEmailExists extends RuntimeException {
	public UserWithSameEmailExists(String message) {
		super(message);
	}
}
