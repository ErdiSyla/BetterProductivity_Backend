package com.erdi.Services;

import com.erdi.DTO.UserDto;
import com.erdi.Exceptions.InvalidEmailException;
import com.erdi.Exceptions.UserWithSameEmailExists;
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

	public ResponseEntity<ApiResponse> signUp(UserDto userDto) {
		String email = userDto.email();
		if(!isValidEmail(email)){
			throw new InvalidEmailException("The email provided is not valid.");
		}else if(userRepository.existsByEmail(email)){
			throw new UserWithSameEmailExists("A user with the same email exists.");
		}
		UserModel userModel = convertDtoToModel(userDto,true);
		userRepository.saveAndFlush(userModel);
		ApiResponse response = new ApiResponse("User created successfully",HttpStatus.CREATED.value());
		return new ResponseEntity<>(response ,HttpStatus.CREATED);
	}

	public boolean isValidEmail(String email) {
		String combinedRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		Pattern pattern = Pattern.compile(combinedRegex);
		return pattern.matcher(email).matches();
	}

	private UserModel convertDtoToModel(UserDto userDto,boolean encrypt){
		if(encrypt){
			return new UserModel(null,userDto.username(),userDto.email(), encoder.encode(userDto.password()));
		}
		return new UserModel(null,userDto.username(),userDto.email(),userDto.password());
	}
}
