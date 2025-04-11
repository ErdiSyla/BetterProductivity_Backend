package com.erdi.Services;

import com.erdi.DTO.AuthKeyDTO;
import com.erdi.DTO.KeyActivity;
import com.erdi.DTO.TokenKeyDTO;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyManagementService {

    private final TokenKeyRepository tokenKeyRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper mapper;

    private static final String AUTH_SERVICE_TOPIC = "auth-keys";
    private static final String VALIDATION_TOPIC = "validation-keys";
    private static final String KEY_CHANGE_TOPIC = "key-change";

    @Transactional
    public void generateAndStoreKeyPair(){
        KeyPair keyPair = generateRSAKeyPair();
        if(keyPair == null){
            log.error("RSA KeyPair generation failed. Aborting key storage and Kafka publishing.");
            return;
        }

        String publicKey = encodeKeyToBase64(keyPair.getPublic().getEncoded());
        String privateKey = encodeKeyToBase64(keyPair.getPrivate().getEncoded());

        TokenKeyModel tokenKey = new TokenKeyModel(null,publicKey,privateKey, KeyActivity.ACTIVE, Instant.now());
        tokenKeyRepository.saveAndFlush(tokenKey);
        log.info("RSA KeyPair successfully stored in the database.");

        publishKeyChanges("New RSA key pair generated");
    }

    @Transactional
    public void markKeysForRemoval(){
        Instant cutoffDate = Instant.now()
                .minus(13, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.MILLIS);
        tokenKeyRepository.updateOldKeysToGrace(cutoffDate);
        log.info("Marked keys older than {} for removal.", cutoffDate);
        publishKeyChanges("Keys marked for removal");
    }

    @Transactional
    public void deleteOldKeys(){
        tokenKeyRepository.deleteOldKeys();
        log.info("Old keys deleted from the database.");
        publishKeyChanges("Keys deleted");
    }

    private KeyPair generateRSAKeyPair(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }catch (NoSuchAlgorithmException e){
            log.error("RSA key generation failed" ,e);
            return null;
        }
    }

    private String encodeKeyToBase64(byte[] key){
        return Base64.getEncoder().encodeToString(key);
    }

    private String convertTokenKeysToJson(List<TokenKeyModel> keys){
        try{
            List<TokenKeyDTO> DTOs = keys.stream()
                    .map(key -> new TokenKeyDTO(key.getKeyId(), key.getPublicKey(),key.getPrivateKey()))
                        .toList();
            return mapper.writeValueAsString(DTOs);
            } catch (JsonProcessingException e){
            log.error("JSON conversion failed,applying fallback format for TokenKeys" ,e);
            return fallBackKeyFormat(keys);
        }
    }

    private String convertAuthKeysToJson(List<TokenKeyModel> keys){
        try{
            List<AuthKeyDTO> DTOs = keys.stream()
                    .map(key -> new AuthKeyDTO(key.getKeyId(),key.getPrivateKey()))
                    .toList();
            return mapper.writeValueAsString(DTOs);
        } catch (JsonProcessingException e) {
            log.error("JSON conversion failed,applying fallback format for AuthKeys", e);
            return fallBackAuthKeyFormat(keys);
        }
    }

    private String fallBackKeyFormat(List<TokenKeyModel> keys){
        StringBuilder sb = new StringBuilder("[\n");
        for(TokenKeyModel key : keys){
            sb.append("  {\n")
                    .append("    \"keyId\": ").append(key.getKeyId()).append(",\n")
                    .append("    \"publicKey\": \"").append(key.getPublicKey()).append("\",\n")
                    .append("    \"privateKey\": \"").append(key.getPrivateKey()).append("\",\n")
                    .append("  },\n");
        }

        if(!keys.isEmpty()){
            sb.setLength(sb.length()-2);
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private String fallBackAuthKeyFormat(List<TokenKeyModel> keys){
        StringBuilder sb = new StringBuilder("[\n");
        for(TokenKeyModel key : keys){
            sb.append("  {\n")
                    .append("    \"keyId\": ").append(key.getKeyId()).append(",\n")
                    .append("    \"privateKey\": \"").append(key.getPrivateKey()).append("\",\n")
                    .append("  },\n");
        }

        if(!keys.isEmpty()){
            sb.setLength(sb.length()-2);
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private void publishKeyChanges(String eventDescription){
        String activeKeysMessage = convertAuthKeysToJson(findAllActiveKeys());
        String allKeysMessage = convertTokenKeysToJson(findAllKeys());
        kafkaProducerService.sendMessage(AUTH_SERVICE_TOPIC,activeKeysMessage);
        kafkaProducerService.sendMessage(VALIDATION_TOPIC, allKeysMessage);
        kafkaProducerService.sendMessage(KEY_CHANGE_TOPIC, eventDescription + " at " + Instant.now());
    }

    private List<TokenKeyModel> findAllActiveKeys(){
        return tokenKeyRepository.findAllActiveKeys();
    }

    private List<TokenKeyModel> findAllKeys(){
        return tokenKeyRepository.findAll();
    }
}
