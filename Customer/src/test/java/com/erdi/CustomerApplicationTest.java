package com.erdi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = CustomerApplication.class)
@TestPropertySource(properties = "KAFKA_CONSUMER_GROUP_ID = test-group")
class CustomerApplicationTest {
	@Test
	void load(){

	}
}
