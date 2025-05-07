package com.erdi.Services;

import com.erdi.DTOs.KeyActivity;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyManagementService {

    private final TokenKeyRepository tokenKeyRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper mapper;
    private final EntityManager entityManager;

    private static final String CUSTOMER_TOPIC = "customer-keys";
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

        entityManager.clear();

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

    private void publishKeyChanges(String eventDescription) {
        String activeKeys;
        String validationKeys;
        Map<Integer, String> activeKeysMap = getActiveKeysMap();
        Map<Integer, String> validationKeysMap = getValidationKeysMap();
        try {
            activeKeys = mapper.writeValueAsString(activeKeysMap);
            validationKeys = mapper.writeValueAsString(validationKeysMap);
        }catch (JsonProcessingException e){
            log.error("JSON serialization failed, falling back to manual builder.", e);
            activeKeys = manualSerializer(activeKeysMap);
            validationKeys = manualSerializer(validationKeysMap);
        }
        kafkaProducerService.sendMessage(CUSTOMER_TOPIC, activeKeys);
        kafkaProducerService.sendMessage(VALIDATION_TOPIC, validationKeys);
        kafkaProducerService.sendMessage(KEY_CHANGE_TOPIC, eventDescription + " at " + Instant.now());
    }

    private String manualSerializer(Map<Integer, String> map){
        StringJoiner sj = new StringJoiner(",","{","}");
        map.forEach((k,v) -> {
            sj.add("\"" + k + "\":\"" + v +"\"");
                });
        return sj.toString();
    }

    private Map<Integer, String> getActiveKeysMap(){
        return mapGenerator(tokenKeyRepository.findAllActiveKeys());
    }

    private Map<Integer, String> getValidationKeysMap(){
        return mapGenerator(tokenKeyRepository.findAll());
    }

    Map<Integer, String> mapGenerator(List<TokenKeyModel> list){
        return list.stream()
                .collect(Collectors.toMap(
                        TokenKeyModel::getKeyId,
                        TokenKeyModel::getPublicKey
                ));
    }
}
