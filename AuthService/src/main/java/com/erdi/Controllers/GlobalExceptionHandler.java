package com.erdi.Controllers;

import com.erdi.Exceptions.InvalidEmailException;
import com.erdi.Exceptions.UserWithSameEmailExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidEmailException.class)
	public ResponseEntity<String> handleInvalidEmailException (InvalidEmailException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserWithSameEmailExists.class)
	public ResponseEntity<String> handleUserWithSameEmailExists(UserWithSameEmailExists ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
	}
}
