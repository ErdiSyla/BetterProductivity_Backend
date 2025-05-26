package com.erdi.Exceptions.Implementation;

import com.erdi.DTOs.ErrorCode;
import com.erdi.Exceptions.Abstract.BaseCustomExceptionClass;

public class JWTExtractionException extends BaseCustomExceptionClass {
    public JWTExtractionException(String message, ErrorCode errorCode){
        super(message,errorCode);
    }
}
