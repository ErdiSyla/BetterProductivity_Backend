package com.erdi.Exceptions;

public class NoActiveKeysAvailableException extends RuntimeException{
    public NoActiveKeysAvailableException(String message){
        super(message);
    }
}
