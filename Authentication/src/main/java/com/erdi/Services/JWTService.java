package com.erdi.Services;

import com.erdi.DTO.TokenKeyDTO;
import com.erdi.Exceptions.Implementation.JWTSigningException;
import com.erdi.Exceptions.Implementation.NoActiveKeysAvailableException;
import com.erdi.Models.ErrorCode;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class JWTService {

    private final KafkaConsumerService kafkaConsumerService;

    public String generateToken(String email){
        Map<String, Object> claims = new HashMap<>();

        TokenKeyDTO tokenKeyDTO = getRandomActiveKey();

        return tokenGenerator(claims,email,tokenKeyDTO);
    }

    private String tokenGenerator (Map<String, Object> claims,
                                   String email,TokenKeyDTO tokenKeyDTO){
        long currentTime =System.currentTimeMillis();
        long expirationDate = currentTime + 1000L *  60L * 60L
                * 24L * 7L * 2L;

        return Jwts.builder()
                .claims().add(claims)
                .subject(email)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(expirationDate))
                .and()
                .header()
                .keyId(tokenKeyDTO.keyId()+"")
                .and()
                .signWith(getPrivateKey(tokenKeyDTO))
                .compact();

    }

    private TokenKeyDTO getRandomActiveKey(){
        List<TokenKeyDTO> activeKeys = kafkaConsumerService.getCachedAuthKeys();
        return Optional.ofNullable(activeKeys)
                .filter(keys -> !keys.isEmpty())
                .map(keys ->{
                        if(keys.size() == 1) {
                            return keys.getFirst();
                    }
                        return keys
                                .get(ThreadLocalRandom.current().nextInt(keys.size()));
                })
                .orElseThrow(() -> {
                    log.error("No active JWT keys available at {}. errorCode: {}",
                            Instant.now(), ErrorCode.JWT_NO_KEYS.getCode());
                    return new NoActiveKeysAvailableException
                            ("No active keys available for JWT generation", ErrorCode.JWT_NO_KEYS);
                });
    }

    private PrivateKey getPrivateKey(TokenKeyDTO tokenKeyDTO){
        try{
            String stringKey = tokenKeyDTO.privateKey();
            byte[] keyBytes = Base64.getDecoder().decode(stringKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error retrieving private key at {}: {}", Instant.now(), e.getMessage());
            throw new JWTSigningException("Error retrieving private key", ErrorCode.JWT_SIGNING,e);
        }
    }

    public void addCookie(HttpServletResponse response, String token){
        ResponseCookie cookie = ResponseCookie.from("AccessToken",token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2419200)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
    }

}
