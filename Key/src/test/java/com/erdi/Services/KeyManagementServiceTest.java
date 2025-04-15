package com.erdi.Services;

import com.erdi.DTO.KeyActivity;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Testable
@ExtendWith(MockitoExtension.class)
class KeyManagementServiceTest {

    @Mock
    private TokenKeyRepository tokenKeyRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KeyManagementService keyManagementService;

    @Test
    void KeyManagementService_generateAndStoreKeyPair_SavesKey(){
        doNothing().when(kafkaProducerService).sendMessage(anyString(),isNull());
        keyManagementService.generateAndStoreKeyPair();

        ArgumentCaptor<TokenKeyModel> captor = ArgumentCaptor.forClass(TokenKeyModel.class);
        verify(tokenKeyRepository,times(1)).saveAndFlush(captor.capture());
        TokenKeyModel tokenKeyModel = captor.getValue();

        assertThat(tokenKeyModel.getPublicKey()).isNotNull();
        assertThat(tokenKeyModel.getPrivateKey()).isNotNull();
        assertThat(tokenKeyModel.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
        assertThat(tokenKeyModel.getTimeOfCreation()).isNotNull();
    }

    @Test
    void KeyManagementService_markKeysForRemoval_CallsRepoTest(){
        doNothing().when(kafkaProducerService).sendMessage(anyString(),isNull());
        keyManagementService.markKeysForRemoval();

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(tokenKeyRepository,times(1)).updateOldKeysToGrace(captor.capture());
        Instant cutoffDate = captor.getValue();
        System.out.println(cutoffDate);

        Instant expectedCutoff = Instant.now()
                .minus(13, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        System.out.println(expectedCutoff);
        long diff = Math.abs(expectedCutoff.toEpochMilli() - cutoffDate.toEpochMilli());
        assertThat(diff).isLessThan(1000);
    }

    @Test
    void KeyManagementService_deleteOldKeys_DeletesTest(){
        doNothing().when(kafkaProducerService).sendMessage(anyString(),isNull());
        keyManagementService.deleteOldKeys();

        verify(tokenKeyRepository,times(1)).deleteOldKeys();
    }
}
