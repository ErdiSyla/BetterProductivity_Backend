package com.erdi.Exceptions.Implementation;

import com.erdi.Models.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class InvalidEmailException extends BaseCustomExceptionClass {

	public InvalidEmailException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
