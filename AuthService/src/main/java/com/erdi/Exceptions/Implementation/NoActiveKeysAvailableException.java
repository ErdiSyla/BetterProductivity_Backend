package com.erdi.Exceptions.Implementation;

import com.erdi.Models.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class NoActiveKeysAvailableException extends BaseCustomExceptionClass {

    public NoActiveKeysAvailableException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}