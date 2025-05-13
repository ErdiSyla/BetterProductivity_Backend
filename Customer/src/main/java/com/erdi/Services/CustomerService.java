package com.erdi.Services;

import com.erdi.DTOs.ApiResponse;
import com.erdi.DTOs.CustomerDTO;
import com.erdi.DTOs.LoginRequestDTO;
import com.erdi.Exceptions.Implementation.CustomerAlreadyExistsException;
import com.erdi.Exceptions.Implementation.InvalidEmailException;
import com.erdi.Exceptions.Implementation.InvalidLogInException;
import com.erdi.Exceptions.Implementation.NoCustomerExistsException;
import com.erdi.Models.CustomerModel;
import com.erdi.Models.ErrorCode;
import com.erdi.Repositories.CustomerRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

	private final BCryptPasswordEncoder encoder;

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private final JWTService jwtService;

	@Transactional
	public ResponseEntity<ApiResponse> signUp(HttpServletResponse response,
											  CustomerDTO customerDto) {
		String email = customerDto.email();

		isValidEmail(email);
		if(doesCustomerExist(email)){
			throw new CustomerAlreadyExistsException
					("A user with the same email exists.", ErrorCode.USER_ALREADY_EXISTS);
		}

		CustomerModel userModel = dtoToModel(customerDto);
		customerRepository.save(userModel);

		String token = jwtService.generateToken(email);
		jwtService.addCookie(response,token);

		log.info("User with email: {} created successfully", email);
		HttpStatus created = HttpStatus.CREATED;
		return ResponseEntity.status(created)
				.body(ApiResponse.builder("User created successfully."
						,created.value()));
	}

	public ResponseEntity<ApiResponse> logIn(HttpServletResponse response,
											 LoginRequestDTO loginRequestDTO) {
		String email = loginRequestDTO.email();
		String password = loginRequestDTO.password();

		CustomerModel customerModel = customerRepository.findCustomerByEmail(email)
				.orElseThrow(() -> {
					log.warn("No user found for email: {}", email);
					 return new NoCustomerExistsException
							("Invalid email or password.", ErrorCode.NO_USER_EXISTS);
				});

		if (!encoder.matches(password, customerModel.getPassword())) {
			log.warn("Invalid password for email: {}",email);
			throw new InvalidLogInException("Invalid email or password.",ErrorCode.INVALID_LOGIN);
		}

		log.info("User {} signed in successfully", email);
		HttpStatus ok = HttpStatus.OK;

		String token = jwtService.generateToken(email);
		jwtService.addCookie(response,token);

		return ResponseEntity.status(ok)
				.body(ApiResponse.builder(
						"Login successful.",ok.value()));
	}

	private CustomerModel dtoToModel(CustomerDTO customerDto){
		return new CustomerModel(null, customerDto.username(), customerDto.email(), encoder.encode(customerDto.password()));
	}

	private void isValidEmail(String email) {
		if(!EMAIL_PATTERN.matcher(email).matches()){
			throw new InvalidEmailException
					("The email provided is not valid.", ErrorCode.INVALID_EMAIL);
		}
	}

	private boolean doesCustomerExist (String email){
        return customerRepository.existsByEmail(email);
	}

}

