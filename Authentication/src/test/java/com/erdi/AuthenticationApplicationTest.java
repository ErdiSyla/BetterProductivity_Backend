package com.erdi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = AuthenticationApplication.class)
@TestPropertySource(properties = "KAFKA_CONSUMER_GROUP_ID = test-group")
class AuthenticationApplicationTest {
	@Test
	void load(){

	}
}
