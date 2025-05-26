package com.erdi.Exceptions.Implementation;

import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;
import com.erdi.DTOs.ErrorCode;

public class JWTSigningException extends BaseCustomExceptionClass {
    public JWTSigningException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
