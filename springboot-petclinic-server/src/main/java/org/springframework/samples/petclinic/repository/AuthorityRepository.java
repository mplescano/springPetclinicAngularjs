package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

	
	List<Authority> findByRole(String role);
	
}
