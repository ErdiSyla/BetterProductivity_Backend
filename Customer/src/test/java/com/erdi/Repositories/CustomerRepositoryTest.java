package com.erdi.Repositories;

import com.erdi.Models.CustomerModel;
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
class CustomerRepositoryTest {

	private final CustomerRepository customerRepository;

	private CustomerModel testCustomerModel1;

	CustomerRepositoryTest(@Autowired CustomerRepository customerRepository){
		this.customerRepository = customerRepository;
	}
	@BeforeEach
	void setUp(){
		testCustomerModel1 = new CustomerModel(null,"Erdi Syla","repotest@gmail.com","test pass");
	}

	@Test
	void UserRepository_Save_ReturnsUserTest(){
		CustomerModel user = customerRepository.saveAndFlush(testCustomerModel1);

		assertThat(user).isNotNull();
		assertThat(user.getId()).isEqualTo(testCustomerModel1.getId());
		assertThat(user.getUsername()).isEqualTo(testCustomerModel1.getUsername());
		assertThat(user.getEmail()).isEqualTo(testCustomerModel1.getEmail());
		assertThat(user.getPassword()).isEqualTo(testCustomerModel1.getPassword());
	}

	@Test
	void UserRepository_FindById_ReturnsUserTest() throws Throwable{
		int id = customerRepository.saveAndFlush(testCustomerModel1).getId();
		CustomerModel returnedUser = customerRepository.findById(id)
						.orElseThrow(Assertions::fail);

		assertThat(returnedUser).isNotNull();
		assertThat(returnedUser.getId()).isEqualTo(testCustomerModel1.getId());
		assertThat(returnedUser.getUsername()).isEqualTo(testCustomerModel1.getUsername());
		assertThat(returnedUser.getEmail()).isEqualTo(testCustomerModel1.getEmail());
		assertThat(returnedUser.getPassword()).isEqualTo(testCustomerModel1.getPassword());
	}

	@Test
	void UserRepository_DeleteById_ReturnsNothingTest(){
		int id = customerRepository.saveAndFlush(testCustomerModel1).getId();
		customerRepository.deleteById(id);
		Optional<CustomerModel> deletedUser = customerRepository.findById(id);

		assertThat(deletedUser).isNotNull();
		assertThat(deletedUser).isEmpty();
	}

	@Test
	void UserRepository_FindByEmail_ReturnsUserTest(){
		String email = customerRepository.saveAndFlush(testCustomerModel1).getEmail();
		CustomerModel returnedUser = null;
		if(customerRepository.findUserByEmail(email).isPresent()) {
			returnedUser = customerRepository.findUserByEmail(email).get();
		}else{
			fail("User could not be found. Was either not stored properly or invalid email");
		}

		assertThat(returnedUser).isNotNull();
		assertThat(returnedUser.getId()).isEqualTo(testCustomerModel1.getId());
		assertThat(returnedUser.getUsername()).isEqualTo(testCustomerModel1.getUsername());
		assertThat(returnedUser.getEmail()).isEqualTo(testCustomerModel1.getEmail());
		assertThat(returnedUser.getPassword()).isEqualTo(testCustomerModel1.getPassword());
	}

	@Test
	void UserRepository_existsByEmail_ReturnsTrueTest(){
		String email = customerRepository.saveAndFlush(testCustomerModel1).getEmail();
		boolean userExists = customerRepository.existsByEmail(email);

		assertThat(userExists).isTrue();
	}

	@Test
	void UserRepository_existsByEmail_ReturnsFalseTest(){
		customerRepository.saveAndFlush(testCustomerModel1);
		boolean userExists = customerRepository.existsByEmail("notActualUser@gmail.com");

		assertThat(userExists).isFalse();
	}
}
