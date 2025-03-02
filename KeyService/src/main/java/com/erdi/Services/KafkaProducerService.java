package com.erdi.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String , String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        try{
            kafkaTemplate.send(topic,message);
            log.info("Message sent to topic {} successfully.", topic);
        }catch (Exception e){
            log.error("Error sending message to Kafka topic {}: {}", topic, e.getMessage());
        }

    }
}
