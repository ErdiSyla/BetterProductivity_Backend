package com.erdi.Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.mockito.Mockito.mock;

@Testable
@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {

    @Autowired
    private KafkaConsumerService mockKafkaConsumerService;

    @InjectMocks
    private JWTService jwtService;

    private KeyPair keyPair1;
    private KeyPair keyPair2;
    private String keyPair1PrivateKeyBase64;
    private String keyPair2PrivateKeyBase64;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair1 = keyGen.generateKeyPair();
        keyPair2 = keyGen.generateKeyPair();

        keyPair1PrivateKeyBase64 = Base64.getEncoder()
                .encodeToString(keyPair1.getPrivate().getEncoded());
        keyPair2PrivateKeyBase64 = Base64.getEncoder()
                .encodeToString(keyPair2.getPrivate().getEncoded())''
    }


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
