package com.erdi.Repositories;

import com.erdi.Models.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Testable
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

	private UserRepository userRepository;

	private UserModel testUserModel1;
	private UserModel testUserModel2;
	private UserModel testUserModel3;

	@BeforeEach
	public void setUp(){
		testUserModel1 = new UserModel(null,"Erdi Syla","repotest@gmail.com","test pass");
		testUserModel1 = new UserModel(null,"Erdi Syla 1","repotest1@gmail.com","test pass 1");
		testUserModel1 = new UserModel(null,"Erdi Syla 2","repotest2@gmail.com","test pass 2");
	}
}
