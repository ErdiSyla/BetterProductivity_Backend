package com.erdi.Repositories;

import com.erdi.Models.UserModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Testable
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

	private final UserRepository userRepository;

	private UserModel testUserModel1;

	public UserRepositoryTest(@Autowired UserRepository userRepository){
		this.userRepository = userRepository;
	}
	@BeforeEach
	public void setUp(){
		testUserModel1 = new UserModel(null,"Erdi Syla","repotest@gmail.com","test pass");
	}

	@Test
	public void UserRepository_Save_ReturnsUserTest(){
		UserModel user = userRepository.saveAndFlush(testUserModel1);

		assertThat(user).isNotNull();
		assertThat(user.getId()).isEqualTo(testUserModel1.getId());
		assertThat(user.getUsername()).isEqualTo(testUserModel1.getUsername());
		assertThat(user.getEmail()).isEqualTo(testUserModel1.getEmail());
		assertThat(user.getPassword()).isEqualTo(testUserModel1.getPassword());
	}

	@Test
	public void UserRepository_FindById_ReturnsUserTest() throws Throwable{
		int id = userRepository.saveAndFlush(testUserModel1).getId();
		UserModel returnedUser = userRepository.findById(id)
						.orElseThrow(Assertions::fail);

		assertThat(returnedUser).isNotNull();
		assertThat(returnedUser.getId()).isEqualTo(testUserModel1.getId());
		assertThat(returnedUser.getUsername()).isEqualTo(testUserModel1.getUsername());
		assertThat(returnedUser.getEmail()).isEqualTo(testUserModel1.getEmail());
		assertThat(returnedUser.getPassword()).isEqualTo(testUserModel1.getPassword());
	}

	@Test
	public void UserRepository_DeleteById_ReturnsNothingTest(){
		int id = userRepository.saveAndFlush(testUserModel1).getId();
		userRepository.deleteById(id);
		Optional<UserModel> deletedUser = userRepository.findById(id);

		assertThat(deletedUser).isNotNull();
		assertThat(deletedUser).isEmpty();
	}

	@Test
	public void UserRepository_FindByEmail_ReturnsUserTest(){
		String email = userRepository.saveAndFlush(testUserModel1).getEmail();
		UserModel returnedUser = null;
		if(userRepository.findUserByEmail(email).isPresent()) {
			returnedUser = userRepository.findUserByEmail(email).get();
		}else{
			fail("User could not be found. Was either not stored properly or invalid email");
		}

		assertThat(returnedUser).isNotNull();
		assertThat(returnedUser.getId()).isEqualTo(testUserModel1.getId());
		assertThat(returnedUser.getUsername()).isEqualTo(testUserModel1.getUsername());
		assertThat(returnedUser.getEmail()).isEqualTo(testUserModel1.getEmail());
		assertThat(returnedUser.getPassword()).isEqualTo(testUserModel1.getPassword());
	}

	@Test
	public void UserRepository_existsByEmail_ReturnsTrue(){
		String email = userRepository.saveAndFlush(testUserModel1).getEmail();
		boolean userExists = userRepository.existsByEmail(email);

		assertThat(userExists).isTrue();
	}

	@Test
	public void UserRepository_existsByEmail_ReturnsFalse(){
		userRepository.saveAndFlush(testUserModel1);
		boolean userExists = userRepository.existsByEmail("notActualUser@gmail.com");

		assertThat(userExists).isFalse();
	}
}
