package com.erdi.Controllers;

import com.erdi.Exceptions.InvalidEmailException;
import com.erdi.Exceptions.InvalidPasswordException;
import com.erdi.Exceptions.NoUserWithEmailException;
import com.erdi.Exceptions.UserWithSameEmailException;
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

	@ExceptionHandler(UserWithSameEmailException.class)
	public ResponseEntity<String> handleUserWithSameEmailException(UserWithSameEmailException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NoUserWithEmailException.class)
	public ResponseEntity<String> handleNoUserWithEmailException(NoUserWithEmailException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.FORBIDDEN);
	}
}
