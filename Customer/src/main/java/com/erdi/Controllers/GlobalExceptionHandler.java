package com.erdi.Controllers;

import com.erdi.Exceptions.Implementation.*;
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

	@ExceptionHandler(CustomerAlreadyExistsException.class)
	public ResponseEntity<String> handleUserWithSameEmailException(CustomerAlreadyExistsException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NoCustomerExistsException.class)
	public ResponseEntity<String> handleNoUserWithEmailException(NoCustomerExistsException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidLogInException.class)
	public ResponseEntity<String> handleInvalidLoginException(InvalidLogInException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(NoActiveKeysAvailableException.class)
	public ResponseEntity<String> handleNoActiveKeysAvailableException(NoActiveKeysAvailableException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(JWTSigningException.class)
	public ResponseEntity<String> handleJWTSigningException(JWTSigningException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
