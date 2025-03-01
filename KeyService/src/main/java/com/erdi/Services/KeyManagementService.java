package com.erdi.Services;

import com.erdi.DTO.KeyActivity;
import com.erdi.Exceptions.InvalidAlgorithmException;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class KeyManagementService {

    private final TokenKeyRepository tokenKeyRepository;
    private final KafkaProducerService kafkaProducerService;

    private static final String AUTH_SERVICE_TOPIC = "auth-service-keys";
    private static final String VALIDATION_TOPIC = "validation-keys";
    private static final String KEY_CHANGE_TOPIC = "key-change";

    @Transactional
    public void generateAndStoreKeyPair(){
        KeyPair keyPair = generateRSAKeyPair();
        String publicKey = encodeKeyToBase64(keyPair.getPublic().getEncoded());
        String privateKey = encodeKeyToBase64(keyPair.getPrivate().getEncoded());

        TokenKeyModel tokenKey = new TokenKeyModel(null,publicKey,privateKey, KeyActivity.ACTIVE, Instant.now());
        tokenKeyRepository.save(tokenKey);
    }

    public List<TokenKeyModel> findAllActiveKeys(){
        return tokenKeyRepository.findAllActiveKeys();
    }
    public List<TokenKeyModel> findAllKeys(){
        return tokenKeyRepository.findAll();
    }

    @Transactional
    public void markKeysForRemoval(){
        Instant cutoffDate = Instant.now()
                .minus(14, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.MILLIS);
        tokenKeyRepository.updateOldKeysToGrace(cutoffDate);
    }

    @Transactional
    public void deleteOldKeys(){
        tokenKeyRepository.deleteOldKeys();
    }

    private KeyPair generateRSAKeyPair(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }catch (NoSuchAlgorithmException e){
            throw new InvalidAlgorithmException("Keys could not be created with the RSA algorithm.");
        }
    }

    private String encodeKeyToBase64(byte[] key){
        return Base64.getEncoder().encodeToString(key);
    }

    private String formatKeyMessage(String publicKey, String privateKey){
        return "{\"publicKey\":\"" + publicKey + "\", \"privateKey\": \"" + privateKey +"\" }";
    }

}
