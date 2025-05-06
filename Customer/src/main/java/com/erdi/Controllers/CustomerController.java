package com.erdi.Controllers;

import com.erdi.DTOs.ApiResponse;
import com.erdi.DTOs.LoginRequestDTO;
import com.erdi.DTOs.CustomerDTO;
import com.erdi.Services.CustomerService;
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
public class CustomerController {

	private final CustomerService customerService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signUp(HttpServletResponse response, @RequestBody CustomerDTO customerDto){
		return customerService.signUp(response, customerDto);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> logIn(HttpServletResponse response, @RequestBody LoginRequestDTO loginRequestDTO){
		return customerService.logIn(response,loginRequestDTO);
	}
}
