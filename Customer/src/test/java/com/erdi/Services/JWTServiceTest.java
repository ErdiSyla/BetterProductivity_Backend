package com.erdi.Services;

import com.erdi.DTO.TokenKeyDTO;
import com.erdi.Exceptions.Implementation.JWTSigningException;
import com.erdi.Exceptions.Implementation.NoActiveKeysAvailableException;
import com.erdi.Models.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
import java.util.*;

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

        Jwt<?, Claims> parsedToken = getParsedToken(token,keyPair1);

        assertThat(email).isEqualTo(parsedToken.getPayload().getSubject());
        assertThat("1").isEqualTo(parsedToken.getHeader().get("kid"));
    }

    @Test
    void JWTService_generateToken_ReturnsResponseWithMultipleKeysTest(){
        TokenKeyDTO tokenKeyDTO1 = new TokenKeyDTO(1,keyPair1PrivateKeyBase64);
        TokenKeyDTO tokenKeyDTO2 = new TokenKeyDTO(2,keyPair2PrivateKeyBase64);
        List<TokenKeyDTO> keys = Arrays.asList(tokenKeyDTO1,tokenKeyDTO2);
        given(mockKafkaConsumerService.getCachedAuthKeys())
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
        given(mockKafkaConsumerService.getCachedAuthKeys())
                .willReturn(Collections.emptyList());

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
        TokenKeyDTO invalidKey = new TokenKeyDTO(3,"not-a-valid-base64-key");
        given(mockKafkaConsumerService.getCachedAuthKeys())
                .willReturn(new ArrayList<>(Collections.singletonList(invalidKey)));

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
