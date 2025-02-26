package com.erdi.Services;

import com.erdi.DTO.KeyActivity;
import com.erdi.Models.TokenKeyModel;
import com.erdi.Repositories.TokenKeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testable
@ExtendWith(MockitoExtension.class)
public class KeyManagementServiceTest {

    @Mock
    private TokenKeyRepository tokenKeyRepository;

    @InjectMocks
    private KeyManagementService keyManagementService;

    @Test
    public void KeyManagementService_generateAndStoreKeyPair_SavesKey(){
        keyManagementService.generateAndStoreKeyPair();

        ArgumentCaptor<TokenKeyModel> captor = ArgumentCaptor.forClass(TokenKeyModel.class);
        verify(tokenKeyRepository,times(1)).save(captor.capture());
        TokenKeyModel tokenKeyModel = captor.getValue();

        assertThat(tokenKeyModel.getPublicKey()).isNotNull();
        assertThat(tokenKeyModel.getPrivateKey()).isNotNull();
        assertThat(tokenKeyModel.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
        assertThat(tokenKeyModel.getTimeOfCreation()).isNotNull();
    }

    @Test
    public void KeyManagementService_findAllActiveKeys_ReturnsActiveKeys(){
        List<TokenKeyModel> expectedKeys = Arrays.asList(new TokenKeyModel(), new TokenKeyModel());
        given(tokenKeyRepository.findAllActiveKeys())
                .willReturn(expectedKeys);

        List<TokenKeyModel> returnedKeys = keyManagementService.findAllActiveKeys();

        assertThat(returnedKeys.size()).isEqualTo(2);
        verify(tokenKeyRepository,times(1)).findAllActiveKeys();
    }

    @Test
    public void KeyManagementService_findAllKeys_ReturnsKeysTest(){
        List<TokenKeyModel> expectedKeys = Arrays.asList(new TokenKeyModel(), new TokenKeyModel());
        given(tokenKeyRepository.findAll())
                .willReturn(expectedKeys);

        List<TokenKeyModel> returnedKeys = keyManagementService.findAllKeys();

        assertThat(returnedKeys.size()).isEqualTo(2);
        verify(tokenKeyRepository,times(1)).findAll();
    }

    @Test
    public void KeyManagementService_markKeysForRemoval_CallsRepoTest(){
        keyManagementService.markKeysForRemoval();

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(tokenKeyRepository,times(1)).updateOldKeysToGrace(captor.capture());
        Instant cutoffDate = captor.getValue();
        System.out.println(cutoffDate);

        Instant expectedCutoff = Instant.now()
                .minus(14, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS);
        System.out.println(expectedCutoff);
        long diff = Math.abs(expectedCutoff.toEpochMilli() - cutoffDate.toEpochMilli());
        assertThat(diff).isLessThan(1000);
    }

    @Test
    public void KeyManagementService_deleteOldKeys_DeletesTest(){
        keyManagementService.deleteOldKeys();

        verify(tokenKeyRepository,times(1)).deleteOldKeys();
    }

}
