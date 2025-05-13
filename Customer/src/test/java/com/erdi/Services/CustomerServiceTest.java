package com.erdi.Services;

import com.erdi.DTOs.LoginRequestDTO;
import com.erdi.DTOs.CustomerDTO;
import com.erdi.Exceptions.Implementation.InvalidEmailException;
import com.erdi.Exceptions.Implementation.InvalidLogInException;
import com.erdi.Exceptions.Implementation.NoCustomerExistsException;
import com.erdi.Exceptions.Implementation.CustomerAlreadyExistsException;
import com.erdi.DTOs.ApiResponse;
import com.erdi.Models.CustomerModel;
import com.erdi.Repositories.CustomerRepository;
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
class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private JWTService jwtService;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private CustomerService customerService;

	private CustomerModel testCustomerModel;
	private CustomerDTO testUser;
	private CustomerDTO invalidUser;
	private LoginRequestDTO loginRequestDTO;
	private HttpServletResponse httpResponse;

	@BeforeEach
	void setUp(){
		testCustomerModel = new CustomerModel(1,"User","servicetest@gmail.com","test pass");
		testUser = new CustomerDTO("User","servicetest@gmail.com","test pass");
		invalidUser = new CustomerDTO("User", "invalidemail","pass");
		loginRequestDTO = new LoginRequestDTO(testUser.email(),testUser.password());
		httpResponse = mock(HttpServletResponse.class);
	}

	@Test
	void UserService_SignUp_ReturnsResponseTest(){
		given(customerRepository.existsByEmail(testUser.email())).willReturn(false);

		ResponseEntity<ApiResponse> response = customerService.signUp(httpResponse,testUser);

		assertThat(response).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("User created successfully.");
		assertThat(response.getBody().status()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		verify(bCryptPasswordEncoder,times(1)).encode(testUser.password());
		verify(customerRepository,times(1)).existsByEmail(testUser.email());
	}

	@Test
	void UserService_SignUp_ThrowsInvalidEmailExceptionTest(){

		InvalidEmailException emailException = assertThrows(InvalidEmailException.class, () -> {
			customerService.signUp(httpResponse,invalidUser);
		});

		assertThat(emailException).isNotNull();
		assertThat(emailException.getMessage()).isEqualTo("The email provided is not valid.");
	}

	@Test
	void UserService_SignUp_ThrowsUserAlreadyExistsExceptionTest(){
		given(customerRepository.existsByEmail(testUser.email())).willReturn(true);

		CustomerAlreadyExistsException emailExists = assertThrows(CustomerAlreadyExistsException.class, () -> {
			customerService.signUp(httpResponse,testUser);
		});

		assertThat(emailExists).isNotNull();
		assertThat(emailExists.getMessage()).isEqualTo("A user with the same email exists.");
		verify(customerRepository,times(1)).existsByEmail(testUser.email());
	}

	@Test
	void UserService_LogIn_ReturnsResponseTest(){
		given(customerRepository.findCustomerByEmail(testUser.email()))
				.willReturn(Optional.of(testCustomerModel));
		given(bCryptPasswordEncoder.matches(testUser.password(), testCustomerModel.getPassword()))
				.willReturn(true);

		ResponseEntity<ApiResponse> response = customerService
				.logIn(httpResponse,loginRequestDTO);

		assertThat(response).isNotNull();
		assertThat(response.getBody().message()).isEqualTo("Login successful.");
		assertThat(response.getBody().status()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


		verify(customerRepository,times(1)).findCustomerByEmail(testUser.email());
		verify(bCryptPasswordEncoder,times(1))
				.matches(testUser.password(), testCustomerModel.getPassword());
	}

	@Test
	void UserService_LogIn_ThrowsNoUserExistsExceptionTest(){

		NoCustomerExistsException noUser = assertThrows(NoCustomerExistsException.class, () -> {
			customerService.logIn(httpResponse,loginRequestDTO);
		});

		assertThat(noUser).isNotNull();
		assertThat(noUser.getMessage()).isEqualTo("Invalid email or password.");
		verify(customerRepository,times(1)).findCustomerByEmail(testUser.email());
	}

	@Test
	void UserService_LogIn_ThrowsInvalidLogInExceptionTest(){
		given(customerRepository.findCustomerByEmail(testUser.email()))
				.willReturn(Optional.of(testCustomerModel));

		InvalidLogInException invalidPassword = assertThrows(InvalidLogInException.class,() -> {
			customerService.logIn(httpResponse,loginRequestDTO);
		});

		assertThat(invalidPassword).isNotNull();
		assertThat(invalidPassword.getMessage()).isEqualTo("Invalid email or password.");
		verify(customerRepository,times(1)).findCustomerByEmail(testUser.email());
		verify(bCryptPasswordEncoder,times(1))
				.matches(testCustomerModel.getPassword(), testUser.password());
	}
}
