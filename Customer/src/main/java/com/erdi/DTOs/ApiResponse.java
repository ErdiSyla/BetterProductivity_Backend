package com.erdi.DTOs;

public record ApiResponse(String message, int status) {

    public static ApiResponse builder(String message, int status){
        return  new ApiResponse(message,status);
    }
}
