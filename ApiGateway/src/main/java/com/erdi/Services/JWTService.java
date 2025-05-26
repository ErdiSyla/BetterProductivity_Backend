package com.erdi.Services;

import com.erdi.DTOs.ErrorCode;
import com.erdi.DTOs.TokenInfoDTO;
import com.erdi.Exceptions.Implementation.JWTExtractionException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JWTService {

    private final KafkaConsumerService kafkaConsumerService;

    private Map<Integer, String> jwtKeys;

    private TokenInfoDTO extractTokenInfoClaim(String token){
        final Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        Date expirationDate = claims.getExpiration();
        return new TokenInfoDTO(email,expirationDate);
    }

    private Claims extractAllClaims(String token) {
        try {
            jwtKeys = kafkaConsumerService.getJwtKeys();
            Integer keyId = Integer.parseInt(extractKeyId(token));
            String publicKeyString = jwtKeys.get(keyId);
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            log.error("Error extracting all claims at {} : {}", Instant.now(), e.getMessage());
            throw new JWTExtractionException("Error extracting information from JWT" , ErrorCode.JWT_EXTRACTION);
        }
    }

    private String extractKeyId(String token){
        JwsHeader header = Jwts.parser()
                .build()
                .parseSignedClaims(token)
                .getHeader();

        return header.getKeyId();
    }
}
