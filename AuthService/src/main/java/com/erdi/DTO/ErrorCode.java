package com.erdi.DTO;

public enum ErrorCode {
    INVALID_EMAIL("EMAIL001"),
    NO_USER_WITH_EMAIL("EMAIL002"),
    USER_WITH_SAME_EMAIL("EMAIL003"),
    INVALID_PASSWORD("PASSWORD001"),
    JWT_NO_KEYS("JWT001");

    private final String code;

    ErrorCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
