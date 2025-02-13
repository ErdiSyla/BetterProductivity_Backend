package com.erdi.Services;

import com.erdi.DTO.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	public ResponseEntity<String> signUp(UserDto userDto) {
		return new ResponseEntity<String>("Created",HttpStatus.CREATED);
	}
}
