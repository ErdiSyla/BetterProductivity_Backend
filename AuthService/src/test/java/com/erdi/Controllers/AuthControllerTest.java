package com.erdi.Controllers;

import com.erdi.DTO.ApiResponse;
import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Services.AuthenticationService;
import com.erdi.Services.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testable
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationService mockAuthenticationService;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private ObjectMapper objectMapper;

	private UserDTO testUser;
	private LoginRequestDTO loginRequestDTO;

	@BeforeEach
	public void setUp() {
		testUser = new UserDTO("Test User", "testUser@gmail.com", "testUserPassword");
		loginRequestDTO = new LoginRequestDTO(testUser.email(),testUser.password());
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void AuthController_SignUp_TestReturnsToken() throws Exception {
		ApiResponse response = new ApiResponse("User created successfully.",HttpStatus.CREATED.value());
		given(mockAuthenticationService.signUp(any(UserDTO.class)))
				.willReturn(new ResponseEntity<>(response,HttpStatus.CREATED));

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().string("{\"message\":\"User created successfully.\",\"status\":201}"));
	}

	@Test
	public void AuthController_SignIn_TestReturnsToken() throws Exception {
		ApiResponse response = new ApiResponse("Login successful.",HttpStatus.OK.value());
		given(mockAuthenticationService.signIn(loginRequestDTO))
				.willReturn(new ResponseEntity<>(response,HttpStatus.OK));

		mockMvc.perform(post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"message\":\"Login successful.\",\"status\":200}"));

	}

	@TestConfiguration
	static class MockConfig {
		@Bean
		public AuthenticationService mockAuthenticationService() {
			return mock(AuthenticationService.class);
		}

		@Bean
		public JWTService mockJWTService(){
			return mock(JWTService.class);
		}
	}
}
