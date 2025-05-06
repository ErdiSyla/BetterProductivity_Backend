package com.erdi.Controllers;

import com.erdi.DTOs.ApiResponse;
import com.erdi.DTOs.LoginRequestDTO;
import com.erdi.DTOs.CustomerDTO;
import com.erdi.Services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerService mockCustomerService;

	@Autowired
	private ObjectMapper objectMapper;

	private CustomerDTO testUser;
	private LoginRequestDTO loginRequestDTO;

	@BeforeEach
	void setUp() {
		testUser = new CustomerDTO("Test User", "testUser@gmail.com", "testUserPassword");
		loginRequestDTO = new LoginRequestDTO(testUser.email(),testUser.password());
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void AuthController_SignUp_TestReturnsToken() throws Exception {
		ApiResponse response = new ApiResponse("User created successfully.",HttpStatus.CREATED.value());
		given(mockCustomerService.signUp(any(HttpServletResponse.class)
				,any(CustomerDTO.class)))
				.willReturn(new ResponseEntity<>(response,HttpStatus.CREATED));

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().string("{\"message\":\"User created successfully.\",\"status\":201}"));
	}

	@Test
	void AuthController_LogIn_TestReturnsToken() throws Exception {
		ApiResponse response = new ApiResponse("Login successful.",HttpStatus.OK.value());
		given(mockCustomerService.logIn(any(HttpServletResponse.class)
				,any(LoginRequestDTO.class)))
				.willReturn(new ResponseEntity<>(response,HttpStatus.OK));

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"message\":\"Login successful.\",\"status\":200}"));

	}

	@TestConfiguration
	static class MockConfig {
		@Bean
		CustomerService mockUserService() {
			return mock(CustomerService.class);
		}
	}
}
