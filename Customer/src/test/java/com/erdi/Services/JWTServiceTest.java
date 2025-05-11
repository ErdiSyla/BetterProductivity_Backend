package com.erdi.Services;

import com.erdi.DTOs.TokenKeyDTO;
import com.erdi.Exceptions.Implementation.JWTSigningException;
import com.erdi.Exceptions.Implementation.NoActiveKeysAvailableException;
import com.erdi.Models.ErrorCode;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private TokenKeyDTO tokenKeyDTO1;
    private TokenKeyDTO tokenKeyDTO2;
    private TokenKeyDTO invalidKey;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair1 = keyGen.generateKeyPair();
        keyPair2 = keyGen.generateKeyPair();

        String keyPair1PrivateKeyBase64 = Base64.getEncoder()
                .encodeToString(keyPair1.getPrivate().getEncoded());
        String keyPair2PrivateKeyBase64 = Base64.getEncoder()
                .encodeToString(keyPair2.getPrivate().getEncoded());

        tokenKeyDTO1 = new TokenKeyDTO(1, keyPair1PrivateKeyBase64);
        tokenKeyDTO2 = new TokenKeyDTO(2, keyPair2PrivateKeyBase64);
        invalidKey = new TokenKeyDTO(3,"not-a-valid-base64-key");
    }


    @Test
    void JWTService_generateToken_ReturnsResponseTest(){
        Map<Integer, String> keys = Map.ofEntries(entry(tokenKeyDTO1.keyId(),tokenKeyDTO1.privateKey()));
        given(mockKafkaConsumerService.getJwtKeys())
                .willReturn(keys);

        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).isNotBlank();

        Jwt<?, Claims> parsedToken = getParsedToken(token,keyPair1);

        assertThat(email).isEqualTo(parsedToken.getPayload().getSubject());
        assertThat("1").isEqualTo(parsedToken.getHeader().get("kid"));
    }

    @Test
    void JWTService_generateToken_ReturnsResponseWithMultipleKeysTest(){
        Map<Integer, String> keys = Map.ofEntries(entry(tokenKeyDTO1.keyId(),tokenKeyDTO1.privateKey()),
                entry(tokenKeyDTO2.keyId(),tokenKeyDTO2.privateKey()));
        given(mockKafkaConsumerService.getJwtKeys())
                .willReturn(keys);

        String email = "multi@example.com";
        String token = jwtService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).isNotBlank();

        String keyId = null;
        try{
            Jwt<?, Claims> parsedToken = getParsedToken(token,keyPair1);

            keyId = (String) parsedToken.getHeader().get("kid");
            assertThat(email).isEqualTo(parsedToken.getPayload().getSubject());
            assertThat(keyId).isEqualTo("1");
        }catch(Exception e){
            Jwt<?, Claims> parsedToken = getParsedToken(token,keyPair2);

            keyId = (String) parsedToken.getHeader().get("kid");
            assertThat(email).isEqualTo(parsedToken.getPayload().getSubject());
            assertThat(keyId).isEqualTo("2");
        }
    }

    @Test
    void JWTService_generateToken_ThrowsNoActiveKeysAvailableExceptionTest(){
        given(mockKafkaConsumerService.getJwtKeys())
                .willReturn(Collections.emptyMap());

        NoActiveKeysAvailableException e = assertThrows(NoActiveKeysAvailableException.class, () -> {
            jwtService.generateToken("test@example.com");
                });

        assertThat(e.getMessage())
                .isEqualTo("No active keys available for JWT generation");
        assertThat(e.getErrorCode())
                .isEqualTo(ErrorCode.JWT_NO_KEYS);
    }

    @Test
    void JWTService_generateToken_ThrowsJWTSigningExceptionTest(){
        Map<Integer, String> keys = Map.ofEntries(entry(invalidKey.keyId(),invalidKey.privateKey()));
        given(mockKafkaConsumerService.getJwtKeys())
                .willReturn(keys);

        JWTSigningException e = assertThrows(JWTSigningException.class, () -> {
            jwtService.generateToken("test@example.com");
        });

        assertThat(e.getMessage()).isEqualTo("Error retrieving private key");
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.JWT_SIGNING);
    }

    private Jws<Claims> getParsedToken(String token, KeyPair keyPair) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token);
    }

}
