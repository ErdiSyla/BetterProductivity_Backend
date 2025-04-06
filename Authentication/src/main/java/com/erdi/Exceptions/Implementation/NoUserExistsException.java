package com.erdi.Exceptions.Implementation;

import com.erdi.Models.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class NoUserExistsException extends BaseCustomExceptionClass {

	public NoUserExistsException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
