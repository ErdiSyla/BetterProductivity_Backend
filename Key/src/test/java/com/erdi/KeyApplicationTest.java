package com.erdi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = KeyApplicationTest.TestConfig.class)
class KeyApplicationTest {

	@Configuration
	static class TestConfig{

	}
	@Test
	void load(){

	}
}
