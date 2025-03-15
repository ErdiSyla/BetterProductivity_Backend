package com.erdi.Exceptions.Implementation;

import com.erdi.Models.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class InvalidPasswordException extends BaseCustomExceptionClass {

	public InvalidPasswordException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
