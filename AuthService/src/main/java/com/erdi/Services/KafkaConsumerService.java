package com.erdi.Services;

import com.erdi.DTO.TokenKeyDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
@Slf4j
public class KafkaConsumerService {

	private volatile List<TokenKeyDTO> cachedAuthKeys;

	@KafkaListener(topics = {"auth-service-keys"}, groupId = "auth-group")
	public void listenAuthKeys(String message){
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			log.info("Received new auth keys : {}", message);
			cachedAuthKeys = objectMapper.readValue(
					message, new TypeReference<>() {
					});
			log.info("Successfully parsed and cached token keys {}", cachedAuthKeys);
		} catch (JsonProcessingException e){
			log.error("Failed to parse token keys from message: {}\nError: {}",message,e.getMessage());
		} catch (Exception e){
			log.error("Unexpected error while processing auth-service-keys message: {}\nError: {}", message, e.getMessage());
		}
	}

	@KafkaListener(topics = "key-change", groupId = "auth-group")
	public void listenKeyChanged(String message){
		try{
			log.info(message);
		}catch (Exception e){
			log.error("Error processing key change message: {}\nError:{}",message,e.getMessage());
		}
	}

}
