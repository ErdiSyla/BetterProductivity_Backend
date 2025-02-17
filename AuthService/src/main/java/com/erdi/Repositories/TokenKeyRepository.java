package com.erdi.Repositories;

import com.erdi.Models.TokenKeyModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenKeyRepository extends JpaRepository<TokenKeyModel,Integer> {

	@Modifying
	@Transactional
	@Query("DELETE FROM TokenKeyModel t WHERE t.keyActivity = 'GRACE'")
	void deleteKeysByActivity();
}
