package com.erdi.Repositories;

import com.erdi.DTO.KeyActivity;
import com.erdi.Models.TokenKeyModel;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testable
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TokenKeyRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	private final TokenKeyRepository tokenKeyRepository;

	private TokenKeyModel activeTokenKeyModel;
	private TokenKeyModel graceTokenKeyModel;
	private TokenKeyModel oldTokenKeyModel;

	public TokenKeyRepositoryTest(@Autowired TokenKeyRepository tokenKeyRepository){
		this.tokenKeyRepository = tokenKeyRepository;
	}

	@BeforeEach
	public void setUp(){
		activeTokenKeyModel = new TokenKeyModel(null,"test public key","test private key",
				KeyActivity.ACTIVE, Instant.now());
		graceTokenKeyModel = new TokenKeyModel(null,"test public key","test private key",
				KeyActivity.GRACE, Instant.now());
		oldTokenKeyModel = new TokenKeyModel(null,"Old public key","Old private key",
				KeyActivity.ACTIVE,Instant.now().minus(15, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS));
	}

	@Test
	public void TokenKeyRepository_Save_ReturnsTokenKeyTest(){
		TokenKeyModel returnedKey = tokenKeyRepository.save(activeTokenKeyModel);

		assertThat(returnedKey).isNotNull();
		assertThat(returnedKey.getKeyId()).isNotNull();
		assertThat(returnedKey.getKeyId()).isGreaterThan(0);
		assertThat(returnedKey.getPublicKey()).isEqualTo(activeTokenKeyModel.getPublicKey());
		assertThat(returnedKey.getPrivateKey()).isEqualTo(activeTokenKeyModel.getPrivateKey());
		assertThat(returnedKey.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
		assertThat(returnedKey.getTimeOfCreation()).isBefore(Instant.now());
	}

	@Test
	public void TokenKeyRepository_FindById_ReturnsTokenKeyTest() throws Throwable{
		int keyId = tokenKeyRepository.save(activeTokenKeyModel).getKeyId();

		TokenKeyModel returnedKey = tokenKeyRepository.findById(keyId)
				.orElseThrow(Assertions::fail);

		assertThat(returnedKey).isNotNull();
		assertThat(returnedKey.getKeyId()).isEqualTo(keyId);
		assertThat(returnedKey.getPublicKey()).isEqualTo(activeTokenKeyModel.getPublicKey());
		assertThat(returnedKey.getPrivateKey()).isEqualTo(activeTokenKeyModel.getPrivateKey());
		assertThat(returnedKey.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
		assertThat(returnedKey.getTimeOfCreation()).isEqualTo(activeTokenKeyModel.getTimeOfCreation());
	}

	@Test
	public void TokenKeyRepository_DeleteById_ReturnsNothing(){
		int keyId = tokenKeyRepository.save(activeTokenKeyModel).getKeyId();
		tokenKeyRepository.deleteById(keyId);

		Optional<TokenKeyModel> deletedKey= tokenKeyRepository.findById(keyId);

		assertThat(deletedKey).isNotNull();
		assertThat(deletedKey).isEmpty();
	}

	@Test
	public void TokenKeyRepository_DeleteOldKeys_DeletesGraceKey(){
		tokenKeyRepository.save(activeTokenKeyModel);
		tokenKeyRepository.save(graceTokenKeyModel);

		tokenKeyRepository.deleteOldKeys();
		List<TokenKeyModel> keysAfterDeletion = tokenKeyRepository.findAll();

		assertThat(keysAfterDeletion).isNotNull();
		assertThat(keysAfterDeletion).isNotEmpty();
		assertThat(keysAfterDeletion.size()).isEqualTo(1);
		assertThat(keysAfterDeletion.getFirst()).isEqualTo(activeTokenKeyModel);
	}

	@Transactional
	@Test
	public void TokenKeyRepository_UpdateOldKeysToGrace_DeletesGraceKey(){
		TokenKeyModel returnedTokenKey = tokenKeyRepository.save(oldTokenKeyModel);
        System.out.println(returnedTokenKey.getTimeOfCreation());
        Instant instant = Instant.now()
                .minus(14, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.MILLIS);
		int i = tokenKeyRepository.updateOldKeysToGrace(instant);
		System.out.println(i);

		entityManager.flush();
		entityManager.clear();

		TokenKeyModel updatedTokenKey = tokenKeyRepository.findById(returnedTokenKey.getKeyId()).get();
        System.out.println(updatedTokenKey.getTimeOfCreation());

		assertThat(updatedTokenKey).isNotNull();
		assertThat(updatedTokenKey.getKeyId()).isEqualTo(returnedTokenKey.getKeyId());
		assertThat(updatedTokenKey.getPublicKey()).isEqualTo(returnedTokenKey.getPublicKey());
		assertThat(updatedTokenKey.getPrivateKey()).isEqualTo(returnedTokenKey.getPrivateKey());
		assertThat(updatedTokenKey.getKeyActivity()).isEqualTo(KeyActivity.GRACE);
		assertThat(updatedTokenKey.getTimeOfCreation()).isEqualTo(returnedTokenKey.getTimeOfCreation());
	}


	@Test
	public void TokenKeyRepository_findAllActiveKeys_ReturnsOnlyActiveKeys(){
		tokenKeyRepository.save(activeTokenKeyModel);
		tokenKeyRepository.save(graceTokenKeyModel);

		List<TokenKeyModel> activeKeys = tokenKeyRepository.findAllActiveKeys();

		assertThat(activeKeys).isNotNull();
		assertThat(activeKeys).isNotEmpty();
		assertThat(activeKeys.size()).isEqualTo(1);
		assertThat(activeKeys.getFirst()).isEqualTo(activeTokenKeyModel);
	}

}