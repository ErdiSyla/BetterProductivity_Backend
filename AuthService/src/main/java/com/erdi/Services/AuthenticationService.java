package com.erdi.Services;

import com.erdi.DTO.ApiResponse;
import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Exceptions.Implementation.InvalidEmailException;
import com.erdi.Exceptions.Implementation.InvalidPasswordException;
import com.erdi.Exceptions.Implementation.NoUserExistsException;
import com.erdi.Exceptions.Implementation.UserAlreadyExistsException;
import com.erdi.Models.ErrorCode;
import com.erdi.Models.UserModel;
import com.erdi.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthenticationService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder encoder;

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder encoder){
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	@Transactional
	public ResponseEntity<ApiResponse> signUp(UserDTO userDto) {
		String email = userDto.email();

		isValidEmail(email);
		ensureUserDoesNotExist(email);

		UserModel userModel = convertDtoToModel(userDto);
		userRepository.save(userModel);

		log.info("User with email: {} created successfully", email);
		HttpStatus created = HttpStatus.CREATED;
		return ResponseEntity.status(created)
				.body(ApiResponse.builder("User created successfully."
						,created.value()));
	}

	public ResponseEntity<ApiResponse> signIn(LoginRequestDTO loginRequestDTO) {
		String email = loginRequestDTO.email();
		String password = loginRequestDTO.password();

		UserModel userModel = userRepository.findUserByEmail(email)
				.orElseThrow(() -> {
					log.warn("No user found for email: {}", email);
					 return new NoUserExistsException
							("Invalid email or password.", ErrorCode.NO_USER_EXISTS);
				});

		if (!encoder.matches(password, userModel.getPassword())) {
			log.warn("Invalid password for email: {}", email);
			throw new InvalidPasswordException
					("Invalid email or password.", ErrorCode.INVALID_PASSWORD);
		}

		ApiResponse apiResponse = new ApiResponse("Login successful.", HttpStatus.OK.value());
		log.info("User {} signed in successfully", email);
		return ResponseEntity.ok(apiResponse);
	}

	private UserModel convertDtoToModel(UserDTO userDto){
		return new UserModel(null,userDto.username(),userDto.email(), encoder.encode(userDto.password()));
	}

	public void addCookie(HttpServletResponse response, String token){
		ResponseCookie cookie = ResponseCookie.from("AccessToken",token)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(2419200)
				.sameSite("Strict")
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
	}

	private void isValidEmail(String email) {
		if(!EMAIL_PATTERN.matcher(email).matches()){
			throw new InvalidEmailException
					("The email provided is not valid.", ErrorCode.INVALID_EMAIL);
		}
	}

	private void ensureUserDoesNotExist(String email){
		boolean alreadyExists = userRepository.existsByEmail(email);
		if(alreadyExists){
			throw new UserAlreadyExistsException
					("A user with the same email exists.", ErrorCode.USER_ALREADY_EXISTS);
		}
	}

}
