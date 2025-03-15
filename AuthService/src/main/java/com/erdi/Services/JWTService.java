package com.erdi.Services;

import com.erdi.Models.ErrorCode;
import com.erdi.DTO.TokenKeyDTO;
import com.erdi.Exceptions.Implementation.NoActiveKeysAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTService {

    private final KafkaConsumerService kafkaConsumerService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private TokenKeyDTO getRandomActiveKey(){
        List<TokenKeyDTO> activeKeys = kafkaConsumerService.getCachedAuthKeys();
        return Optional.ofNullable(activeKeys)
                .filter(keys -> !keys.isEmpty())
                .map(keys ->{
                        if(keys.size() == 1) {
                            return keys.getFirst();
                    }
                        return keys
                                .get(ThreadLocalRandom.current().nextInt(keys.size()));
                })
                .orElseThrow(() -> {
                    log.error("No active JWT keys available at {}. errorCode: {}",
                            Instant.now(), ErrorCode.JWT_NO_KEYS.getCode());
                    return new NoActiveKeysAvailableException
                            ("No active keys available for JWT generation", ErrorCode.JWT_NO_KEYS);
                });
    }

}
