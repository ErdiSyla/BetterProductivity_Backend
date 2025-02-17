package com.erdi.Repositories;

import com.erdi.Models.KeyActivity;
import com.erdi.Models.TokenKeyModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testable
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TokenKeyRepositoryTest {

	private final TokenKeyRepository tokenKeyRepository;

	private TokenKeyModel testTokenKeyModel;

	public TokenKeyRepositoryTest(@Autowired TokenKeyRepository tokenKeyRepository){
		this.tokenKeyRepository = tokenKeyRepository;
	}

	@BeforeEach
	public void setUp(){
		testTokenKeyModel = new TokenKeyModel(null,"test public key","test private key",
				KeyActivity.ACTIVE, Instant.now());
	}

	@Test
	public void TokenKeyRepository_Save_ReturnsTokenKeyTest(){
		TokenKeyModel returnedKey = tokenKeyRepository.save(testTokenKeyModel);

		assertThat(returnedKey).isNotNull();
		assertThat(returnedKey.getKeyId()).isNotNull();
		assertThat(returnedKey.getKeyId()).isGreaterThan(0);
		assertThat(returnedKey.getPublicKey()).isEqualTo(testTokenKeyModel.getPublicKey());
		assertThat(returnedKey.getPrivateKey()).isEqualTo(testTokenKeyModel.getPrivateKey());
		assertThat(returnedKey.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
		assertThat(returnedKey.getTimeOfCreation()).isBefore(Instant.now());
	}

	@Test
	public void TokenKeyRepository_FindById_ReturnsTokenKeyTest() throws Throwable{
		int keyId = tokenKeyRepository.save(testTokenKeyModel).getKeyId();

		TokenKeyModel returnedKey = tokenKeyRepository.findById(keyId)
				.orElseThrow(Assertions::fail);

		assertThat(returnedKey).isNotNull();
		assertThat(returnedKey.getKeyId()).isEqualTo(keyId);
		assertThat(returnedKey.getPublicKey()).isEqualTo(testTokenKeyModel.getPublicKey());
		assertThat(returnedKey.getPrivateKey()).isEqualTo(testTokenKeyModel.getPrivateKey());
		assertThat(returnedKey.getKeyActivity()).isEqualTo(KeyActivity.ACTIVE);
		assertThat(returnedKey.getTimeOfCreation()).isEqualTo(testTokenKeyModel.getTimeOfCreation());
	}

	@Test
	public void TokenKeyRepository_DeleteById_ReturnsNothing(){
		int keyId = tokenKeyRepository.save(testTokenKeyModel).getKeyId();
		tokenKeyRepository.deleteById(keyId);

		Optional<TokenKeyModel> deletedKey= tokenKeyRepository.findById(keyId);

		assertThat(deletedKey).isNotNull();
		assertThat(deletedKey).isEmpty();
	}

	@Test
}
