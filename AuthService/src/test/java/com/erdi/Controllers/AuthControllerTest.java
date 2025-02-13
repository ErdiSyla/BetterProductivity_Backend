package com.erdi.Controllers;

import com.erdi.DTO.UserDto;
import com.erdi.Services.AuthenticationService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
	private ObjectMapper objectMapper;

	private UserDto testUser;

	@BeforeEach
	public void setUp() {
		testUser = new UserDto("Test User", "testUser@gmail.com", "testUserPassword");
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void AuthController_SignUp_TestReturnsToken() throws Exception {
		when(mockAuthenticationService.signUp(any(UserDto.class)))
				.thenReturn(new ResponseEntity<>("Created", HttpStatus.CREATED));

		mockMvc.perform(post("/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(testUser)))
				.andExpect(status().isCreated())
				.andExpect(content().string("Created"));
	}

	@TestConfiguration
	static class MockConfig {
		@Bean
		public AuthenticationService mockAuthenticationService() {
			return mock(AuthenticationService.class);
		}
	}
}
