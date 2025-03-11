package com.erdi.Models;

import lombok.Getter;

@Getter
public record ApiResponse(String message, int status) {
}
