package com.erdi.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private volatile Map<Integer,String> jwtKeys = Collections.emptyMap();

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"validation-keys"}, groupId = "${KAFKA_CONSUMER_GROUP_ID}")
    public void listenAuthKeys(String message){
        try {
            log.info("Received new validation keys : {}", message);
            jwtKeys = Collections.unmodifiableMap(objectMapper.readValue(
                    message, new TypeReference<Map<Integer,String>>() {
                    }));
            log.info("Successfully parsed validation keys {}", jwtKeys);
        } catch (JsonProcessingException e){
            log.error("Failed to parse token keys from message: {}\nError: {}",message,e.getMessage());
        } catch (Exception e){
            log.error("Unexpected error while processing message from customer keys topic: {}\nError: {}",
                    message, e.getMessage());
        }
    }


    @KafkaListener(topics = "key-change", groupId = "${KAFKA_CONSUMER_GROUP_ID}")
    public void listenKeyChanged(String message){
        try{
            log.info(message);
        }catch (Exception e){
            log.error("Error processing message from key-change topic: {}\nError:{}",message,e.getMessage());
        }
    }

}