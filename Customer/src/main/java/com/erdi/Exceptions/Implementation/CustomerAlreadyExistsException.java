package com.erdi.Exceptions.Implementation;

import com.erdi.DTOs.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class CustomerAlreadyExistsException extends BaseCustomExceptionClass {

	public CustomerAlreadyExistsException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
