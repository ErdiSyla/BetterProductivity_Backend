package com.erdi.Exceptions.Implementation;

import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;
import com.erdi.Models.ErrorCode;

public class JWTSigningException extends BaseCustomExceptionClass {
    public JWTSigningException(String message, ErrorCode errorCode, Exception e) {
        super(message, errorCode);
    }
}
