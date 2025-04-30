package com.erdi.Controllers;

import com.erdi.DTO.ApiResponse;
import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Services.UserService;
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

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signUp(HttpServletResponse response, @RequestBody UserDTO userDto){
		return userService.signUp(response,userDto);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> logIn(HttpServletResponse response, @RequestBody LoginRequestDTO loginRequestDTO){
		return userService.logIn(response,loginRequestDTO);
	}
}
