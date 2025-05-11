package com.erdi.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ExtraBeanConfig {
	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper();
	}
}