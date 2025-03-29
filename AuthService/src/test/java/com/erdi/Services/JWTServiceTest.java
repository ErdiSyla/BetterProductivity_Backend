package com.erdi.Services;

import com.erdi.DTO.TokenKeyDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Testable
@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Mock
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
                .encodeToString(keyPair2.getPrivate().getEncoded());
    }


    @Test
    void JWTService_generateToken_ReturnsResponseTest(){
        TokenKeyDTO tokenKeyDTO = new TokenKeyDTO(1,keyPair1PrivateKeyBase64);
        given(mockKafkaConsumerService.getCachedAuthKeys())
                .willReturn(new LinkedList<>(Collections.singletonList(tokenKeyDTO)));

        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).isNotBlank();

        Jwt<?, Claims> parsedToken = Jwts.parser()
                .verifyWith(keyPair1.getPublic())
                .build()
                .parseSignedClaims(token);

        assertThat(email).isEqualTo(parsedToken.getPayload().getSubject());
        assertThat("1").isEqualTo(parsedToken.getHeader().get("kid"));
    }

}
