package com.erdi.Repositories;

import com.erdi.Models.TokenKeyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenKeyRepository extends JpaRepository<TokenKeyModel,Integer> {
}
