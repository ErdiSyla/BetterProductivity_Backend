package com.erdi.Services;

import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Exceptions.Implementation.InvalidEmailException;
import com.erdi.Exceptions.Implementation.InvalidLogInException;
import com.erdi.Exceptions.Implementation.NoUserExistsException;
import com.erdi.Exceptions.Implementation.UserAlreadyExistsException;
import com.erdi.DTO.ApiResponse;
import com.erdi.Models.UserModel;
import com.erdi.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Testable
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JWTService jwtService;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private AuthenticationService authenticationService;

	private UserModel testUserModel;
	private UserDTO testUser;
	private UserDTO invalidUser;
	private LoginRequestDTO loginRequestDTO;
	private HttpServletResponse httpResponse;

	@BeforeEach
	void setUp(){
		testUserModel = new UserModel(1,"User","servicetest@gmail.com","test pass");
		testUser = new UserDTO("User","servicetest@gmail.com","test pass");
		invalidUser = new UserDTO("User", "invalidemail","pass");
		loginRequestDTO = new LoginRequestDTO(testUser.email(),testUser.password());
		httpResponse = mock(HttpServletResponse.class);
	}

	@Test
	void AuthenticationService_SignUp_ReturnsResponseTest(){
		given(userRepository.existsByEmail(testUser.email())).willReturn(false);

		ResponseEntity<ApiResponse> response = authenticationService.signUp(httpResponse,testUser);

		assertThat(response).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("User created successfully.");
		assertThat(response.getBody().status()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		verify(bCryptPasswordEncoder,times(1)).encode(testUser.password());
		verify(userRepository,times(1)).existsByEmail(testUser.email());
	}

	@Test
	void AuthenticationService_SignUp_ThrowsInvalidEmailExceptionTest(){

		InvalidEmailException emailException = assertThrows(InvalidEmailException.class, () -> {
			authenticationService.signUp(httpResponse,invalidUser);
		});

		assertThat(emailException).isNotNull();
		assertThat(emailException.getMessage()).isEqualTo("The email provided is not valid.");
	}

	@Test
	void AuthenticationService_SignUp_ThrowsUserAlreadyExistsExceptionTest(){
		given(userRepository.existsByEmail(testUser.email())).willReturn(true);

		UserAlreadyExistsException emailExists = assertThrows(UserAlreadyExistsException.class, () -> {
			authenticationService.signUp(httpResponse,testUser);
		});

		assertThat(emailExists).isNotNull();
		assertThat(emailExists.getMessage()).isEqualTo("A user with the same email exists.");
		verify(userRepository,times(1)).existsByEmail(testUser.email());
	}

	@Test
	void AuthenticationService_LogIn_ReturnsResponseTest(){
		given(userRepository.findUserByEmail(testUser.email()))
				.willReturn(Optional.of(testUserModel));
		given(bCryptPasswordEncoder.matches(testUser.password(),testUserModel.getPassword()))
				.willReturn(true);

		ResponseEntity<ApiResponse> response = authenticationService
				.logIn(httpResponse,loginRequestDTO);

		assertThat(response).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Login successful.");
		assertThat(response.getBody().status()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


		verify(userRepository,times(1)).findUserByEmail(testUser.email());
		verify(bCryptPasswordEncoder,times(1))
				.matches(testUser.password(),testUserModel.getPassword());
	}

	@Test
	void AuthenticationService_LogIn_ThrowsNoUserExistsExceptionTest(){

		NoUserExistsException noUser = assertThrows(NoUserExistsException.class, () -> {
			authenticationService.logIn(httpResponse,loginRequestDTO);
		});

		assertThat(noUser).isNotNull();
		assertThat(noUser.getMessage()).isEqualTo("Invalid email or password.");
		verify(userRepository,times(1)).findUserByEmail(testUser.email());
	}

	@Test
	void AuthenticationService_LogIn_ThrowsInvalidLogInExceptionTest(){
		given(userRepository.findUserByEmail(testUser.email()))
				.willReturn(Optional.of(testUserModel));

		InvalidLogInException invalidPassword = assertThrows(InvalidLogInException.class,() -> {
			authenticationService.logIn(httpResponse,loginRequestDTO);
		});

		assertThat(invalidPassword).isNotNull();
		assertThat(invalidPassword.getMessage()).isEqualTo("Invalid email or password.");
		verify(userRepository,times(1)).findUserByEmail(testUser.email());
		verify(bCryptPasswordEncoder,times(1))
				.matches(testUserModel.getPassword(), testUser.password());
	}
}
