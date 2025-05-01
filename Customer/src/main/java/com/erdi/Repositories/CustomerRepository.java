package com.erdi.Repositories;

import com.erdi.Models.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel,Integer> {

	Optional<CustomerModel> findUserByEmail(String email);

	boolean existsByEmail(String email);
}
