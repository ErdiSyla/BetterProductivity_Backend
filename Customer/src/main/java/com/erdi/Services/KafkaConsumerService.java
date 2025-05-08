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


	@KafkaListener(topics = "key-change", groupId = "${KAFKA_CONSUMER_GROUP_ID}")
	public void listenKeyChanged(String message){
		try{
			log.info(message);
		}catch (Exception e){
			log.error("Error processing message from key-change topic: {}\nError:{}",message,e.getMessage());
		}
	}

}
