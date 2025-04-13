package com.erdi.Controllers;

import com.erdi.DTO.ApiResponse;
import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Services.AuthenticationService;
import com.erdi.Services.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	private final JWTService jwtService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signUp(HttpServletResponse response, @RequestBody UserDTO userDto){
		String value = jwtService.generateToken(userDto.email());
		authenticationService.addCookie(response,value);
		return authenticationService.signUp(userDto);
	}

	@PostMapping("/signin")
	public ResponseEntity<ApiResponse> signIn(HttpServletResponse response, @RequestBody LoginRequestDTO loginRequestDTO){
		String value = jwtService.generateToken(loginRequestDTO.email());
		authenticationService.addCookie(response,value);
		return authenticationService.signIn(loginRequestDTO);
	}
}
