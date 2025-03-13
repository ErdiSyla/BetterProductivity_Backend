package com.erdi.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTService {

    private final KafkaConsumerService kafkaConsumerService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

}
