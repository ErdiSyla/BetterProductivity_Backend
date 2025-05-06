package com.erdi.Services;

import com.erdi.DTOs.TokenKeyDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Getter
@Service
@Slf4j
public class KafkaConsumerService {

	private volatile List<TokenKeyDTO> jwtKeys = Collections.emptyList();

	@Autowired
	private ObjectMapper objectMapper;

	@KafkaListener(topics = {"customer-keys"}, groupId = "${KAFKA_CONSUMER_GROUP_ID}")
	public void listenAuthKeys(String message){
		try {
			log.info("Received new customer keys : {}", message);
			jwtKeys = Collections.unmodifiableList(objectMapper.readValue(
					message, new TypeReference<List<TokenKeyDTO>>() {
					}));
			log.info("Successfully parsed token keys {}", jwtKeys);
		} catch (JsonProcessingException e){
			log.error("Failed to parse token keys from message: {}\nError: {}",message,e.getMessage());
		} catch (Exception e){
			log.error("Unexpected error while processing message from customer-keys topic: {}\nError: {}",
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
