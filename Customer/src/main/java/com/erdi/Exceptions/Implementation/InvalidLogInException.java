package com.erdi.Exceptions.Implementation;

import com.erdi.DTOs.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class InvalidLogInException extends BaseCustomExceptionClass {

	public InvalidLogInException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
