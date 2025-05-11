package com.erdi.DTOs;

import java.util.Date;

public record TokenInfoDTO(String email, Date expiration) {
}
