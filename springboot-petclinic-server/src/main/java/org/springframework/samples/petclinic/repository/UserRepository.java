package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	
	List<User> findByUsername(String username);

	Integer deleteByUsername(String username);
}
