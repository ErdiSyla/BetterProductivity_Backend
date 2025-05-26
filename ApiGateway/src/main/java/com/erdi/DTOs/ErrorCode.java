package com.erdi.DTOs;

import lombok.Getter;

@Getter
public enum ErrorCode {

    JWT_EXTRACTION("JWT003");

    private final String code;

    ErrorCode(String code){
        this.code = code;
    }
}