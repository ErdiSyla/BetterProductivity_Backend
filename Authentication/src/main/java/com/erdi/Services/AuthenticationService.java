package com.erdi.Services;

import com.erdi.DTO.ApiResponse;
import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Exceptions.Implementation.InvalidEmailException;
import com.erdi.Exceptions.Implementation.InvalidLogInException;
import com.erdi.Exceptions.Implementation.NoUserExistsException;
import com.erdi.Exceptions.Implementation.UserAlreadyExistsException;
import com.erdi.Models.ErrorCode;
import com.erdi.Models.UserModel;
import com.erdi.Repositories.UserRepository;
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
public class AuthenticationService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder encoder;

	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private final JWTService jwtService;

	@Transactional
	public ResponseEntity<ApiResponse> signUp(HttpServletResponse response,
											  UserDTO userDto) {
		String email = userDto.email();

		isValidEmail(email);
		assertUserDoesNotExist(email);

		UserModel userModel = convertDtoToModel(userDto);
		userRepository.save(userModel);

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

		UserModel userModel = userRepository.findUserByEmail(email)
				.orElseThrow(() -> {
					log.warn("No user found for email: {}", email);
					 return new NoUserExistsException
							("Invalid email or password.", ErrorCode.NO_USER_EXISTS);
				});

		if (!encoder.matches(password, userModel.getPassword())) {
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

	private UserModel convertDtoToModel(UserDTO userDto){
		return new UserModel(null,userDto.username(),userDto.email(), encoder.encode(userDto.password()));
	}

	private void isValidEmail(String email) {
		if(!EMAIL_PATTERN.matcher(email).matches()){
			throw new InvalidEmailException
					("The email provided is not valid.", ErrorCode.INVALID_EMAIL);
		}
	}

	private void assertUserDoesNotExist(String email){
		boolean alreadyExists = userRepository.existsByEmail(email);
		if(alreadyExists){
			throw new UserAlreadyExistsException
					("A user with the same email exists.", ErrorCode.USER_ALREADY_EXISTS);
		}
	}

}

