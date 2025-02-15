package com.erdi.Services;

import com.erdi.DTO.UserDto;
import com.erdi.Exceptions.InvalidEmailException;
import com.erdi.Exceptions.UserWithSameEmailExists;
import com.erdi.Models.ApiResponse;
import com.erdi.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Testable
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private AuthenticationService authenticationService;

	private UserDto testUser;
	private UserDto invalidUser;

	@BeforeEach
	public void setUp(){
		testUser = new UserDto("User","servicetest@gmail.com","test pass");
		invalidUser = new UserDto("User", "invalidemail","pass");
	}

	@Test
	public void AuthenticationService_SignUp_ReturnsResponseTest(){
		given(userRepository.existsByEmail(testUser.email())).willReturn(false);

		ResponseEntity<ApiResponse> response = authenticationService.signUp(testUser);

		assertThat(response).isNotNull();
		assertThat(response.getBody().getMessage()).isEqualTo("User created successfully");
		assertThat(response.getBody().getStatus()).isEqualTo(201);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		verify(bCryptPasswordEncoder,times(1)).encode(testUser.password());
		verify(userRepository,times(1)).existsByEmail(testUser.email());
	}

	@Test
	public void AuthenticationService_SignUp_ThrowsInvalidEmailException(){

		InvalidEmailException emailException = assertThrows(InvalidEmailException.class, () -> {
			authenticationService.signUp(invalidUser);
		});

		assertThat(emailException).isNotNull();
		assertThat(emailException.getMessage()).isEqualTo("The email provided is not valid.");
	}

	@Test
	public void AuthenticationService_SignUp_ThrowsUserWithSameEmailExistsException(){
		given(userRepository.existsByEmail(testUser.email())).willReturn(true);

		UserWithSameEmailExists emailExists = assertThrows(UserWithSameEmailExists.class, () -> {
			authenticationService.signUp(testUser);
		});

		assertThat(emailExists).isNotNull();
		assertThat(emailExists.getMessage()).isEqualTo("A user with the same email exists.");
		verify(userRepository,times(1)).existsByEmail(testUser.email());
	}
}
