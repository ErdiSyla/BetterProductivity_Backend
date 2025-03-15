package com.erdi.Exceptions.Implementation;

import com.erdi.Models.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class UserAlreadyExistsException extends BaseCustomExceptionClass {

	public UserAlreadyExistsException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
