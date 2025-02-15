package com.erdi.Repositories;

import com.erdi.Models.UserModel;
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
	public void UserRepository_FindById_ReturnsUserTest(){
		int id = userRepository.saveAndFlush(testUserModel1).getId();
		UserModel returnedUser = null;
		if(userRepository.findById(id).isPresent()) {
			returnedUser = userRepository.findById(id).get();
		} else {
			fail("User was not saved properly");
		}

		assertThat(returnedUser).isNotNull();
		assertThat(returnedUser.getId()).isEqualTo(testUserModel1.getId());
		assertThat(returnedUser.getUsername()).isEqualTo(testUserModel1.getUsername());
		assertThat(returnedUser.getEmail()).isEqualTo(testUserModel1.getEmail());
		assertThat(returnedUser.getPassword()).isEqualTo(testUserModel1.getPassword());
	}

	@Test
	public void UserRepository_Delete_ReturnsNothingTest(){
		int id = userRepository.saveAndFlush(testUserModel1).getId();
		userRepository.deleteById(id);
		Optional<UserModel> deletedUser = userRepository.findById(id);

		assertThat(deletedUser).isNotNull();
		assertThat(deletedUser).isEmpty();
	}
}
