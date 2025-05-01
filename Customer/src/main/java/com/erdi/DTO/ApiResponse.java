package com.erdi.DTO;

public record ApiResponse(String message, int status) {

    public static ApiResponse builder(String message, int status){
        return  new ApiResponse(message,status);
    }
}
