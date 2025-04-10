package com.erdi.Config;

import com.erdi.Services.KeyManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeyManagementConfig {

    private final KeyManagementService keyManagementService;


    @Bean
    public CommandLineRunner runKeyManagementJob(){
        return args -> {
            keyManagementService.markKeysForRemoval();
            keyManagementService.deleteOldKeys();
            keyManagementService.generateAndStoreKeyPair();

            System.exit(0);
        };
    }
}
