package com.erdi.Exceptions.Abstract;

import com.erdi.DTOs.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseCustomExceptionClass extends RuntimeException{

    private final ErrorCode errorCode;

    public BaseCustomExceptionClass(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }

}
