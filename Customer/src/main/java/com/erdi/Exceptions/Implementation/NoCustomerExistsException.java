package com.erdi.Exceptions.Implementation;

import com.erdi.DTOs.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class NoCustomerExistsException extends BaseCustomExceptionClass {

	public NoCustomerExistsException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
