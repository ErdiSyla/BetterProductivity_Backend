package com.erdi.Repositories;

import com.erdi.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Integer> {

	Optional<UserModel> findUserByEmail(String email);

	boolean existsByEmail(String email);
}
