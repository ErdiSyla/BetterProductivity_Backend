package com.erdi.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@Testable
@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {

    @Autowired
    private KafkaConsumerService mockKafkaConsumerService;

    @InjectMocks
    private JWTService jwtService;

    @Test
    public void JWTService_generateToken_ReturnsResponseTest(){

    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public KafkaConsumerService mockKafkaConsumerService() {
            return mock(KafkaConsumerService.class);
        }
    }
}
