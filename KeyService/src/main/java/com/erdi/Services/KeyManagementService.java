package com.erdi.Services;

import com.erdi.DTO.KeyActivity;
import com.erdi.DTO.TokenKeyDTO;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Slf4j
public class KeyManagementService {

    private final TokenKeyRepository tokenKeyRepository;
    private final KafkaProducerService kafkaProducerService;

    private static final String AUTH_SERVICE_TOPIC = "auth-service-keys";
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

        String activeKeysMessage = convertKeysToJson(findAllActiveKeys());
        String allKeysMessage = convertKeysToJson(findAllKeys());

        kafkaProducerService.sendMessage(AUTH_SERVICE_TOPIC,activeKeysMessage);
        kafkaProducerService.sendMessage(VALIDATION_TOPIC, allKeysMessage);
        kafkaProducerService.sendMessage(KEY_CHANGE_TOPIC, "New RSA key pair generated at " + Instant.now());
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
        log.info("Marked keys older than {} for removal.", cutoffDate);

        String activeKeysMessage = convertKeysToJson(findAllActiveKeys());
        String allKeysMessage = convertKeysToJson(findAllKeys());

        kafkaProducerService.sendMessage(AUTH_SERVICE_TOPIC,activeKeysMessage);
        kafkaProducerService.sendMessage(VALIDATION_TOPIC, allKeysMessage);
        kafkaProducerService.sendMessage(KEY_CHANGE_TOPIC, "Keys have been marked for removal at " + Instant.now());
    }

    @Transactional
    public void deleteOldKeys(){
        tokenKeyRepository.deleteOldKeys();
        log.info("Old keys deleted from the database.");

        String activeKeysMessage = convertKeysToJson(findAllActiveKeys());
        String allKeysMessage = convertKeysToJson(findAllKeys());

        kafkaProducerService.sendMessage(AUTH_SERVICE_TOPIC,activeKeysMessage);
        kafkaProducerService.sendMessage(VALIDATION_TOPIC, allKeysMessage);
        kafkaProducerService.sendMessage(KEY_CHANGE_TOPIC, "Keys deleted at " + Instant.now());
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

    private String convertKeysToJson(List<TokenKeyModel> keys){
        try{
            List<TokenKeyDTO> DTOs = keys.stream()
                    .map(key -> new TokenKeyDTO(key.getKeyId(), key.getPublicKey(),key.getPrivateKey()))
                        .toList();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(DTOs);
            }catch (JsonProcessingException e){
            log.error("JSON conversion failed,applying fallback format" ,e);
            return fallBackKeyFormat(keys);
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

}
