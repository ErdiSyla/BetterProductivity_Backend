package com.erdi.Controllers;

import com.erdi.DTO.UserDto;
import com.erdi.Services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody UserDto userDto){
		return authenticationService.signUp(userDto);
	}
}
