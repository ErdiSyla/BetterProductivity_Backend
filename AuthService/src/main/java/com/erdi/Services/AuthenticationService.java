package com.erdi.Services;

import com.erdi.DTO.LoginRequestDTO;
import com.erdi.DTO.UserDTO;
import com.erdi.Exceptions.InvalidEmailException;
import com.erdi.Exceptions.InvalidPasswordException;
import com.erdi.Exceptions.NoUserWithEmailException;
import com.erdi.Exceptions.UserWithSameEmailException;
import com.erdi.Models.ApiResponse;
import com.erdi.Models.UserModel;
import com.erdi.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder encoder;

	public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder encoder){
		this.userRepository = userRepository;
		this.encoder = encoder;
	}

	public ResponseEntity<ApiResponse> signUp(UserDTO userDto) {
		String email = userDto.email();

		if(!isValidEmail(email)){
			throw new InvalidEmailException("The email provided is not valid.");
		}

		if(userRepository.existsByEmail(email)){
			throw new UserWithSameEmailException("A user with the same email exists.");
		}

		UserModel userModel = convertDtoToModel(userDto,true);
		userRepository.saveAndFlush(userModel);

		ApiResponse response = new ApiResponse("User created successfully.",HttpStatus.CREATED.value());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	public ResponseEntity<ApiResponse> signIn(LoginRequestDTO loginRequestDTO) {
		String email = loginRequestDTO.email();
		String password = loginRequestDTO.password();

		UserModel userModel = userRepository.findUserByEmail(email)
				.orElseThrow(() -> new NoUserWithEmailException(
						"There is no existing userModel that holds this email. Please sign up."));

		if (!encoder.matches(password, userModel.getPassword())) {
			throw new InvalidPasswordException("The password entered is invalid. Please try again.");
		}

		ApiResponse apiResponse = new ApiResponse("Login successful.", HttpStatus.OK.value());
		return ResponseEntity.ok(apiResponse);
	}

	public boolean isValidEmail(String email) {
		String combinedRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		Pattern pattern = Pattern.compile(combinedRegex);
		return pattern.matcher(email).matches();
	}

	private UserModel convertDtoToModel(UserDTO userDto, boolean encrypt){
		if(encrypt){
			return new UserModel(null,userDto.username(),userDto.email(), encoder.encode(userDto.password()));
		}
		return new UserModel(null,userDto.username(),userDto.email(),userDto.password());
	}
}
