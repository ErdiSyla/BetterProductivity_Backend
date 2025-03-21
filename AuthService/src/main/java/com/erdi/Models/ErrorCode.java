package com.erdi.Models;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_EMAIL("EMAIL001"),
    NO_USER_EXISTS("EMAIL002"),
    USER_ALREADY_EXISTS("EMAIL003"),
    INVALID_PASSWORD("PASSWORD001"),
    JWT_NO_KEYS("JWT001");

    private final String code;

    ErrorCode(String code){
        this.code = code;
    }

}
